import socket
import json
import threading
import sys
import time

SERVER_HOST = 'localhost'
SERVER_PORTS = [5000, 5001] 
UDP_MULTICAST_GROUP = '224.1.1.1'
UDP_MULTICAST_PORT = 5001

class VoterClient:
    def __init__(self):
        # Apenas inicializa o socket multicast
        self.udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.udp_socket.settimeout(1.0)
        
        # Configurar para receber multicast
        self.udp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        
        # Linux específico: SO_REUSEPORT para permitir múltiplos listeners
        if hasattr(socket, 'SO_REUSEPORT'):
            self.udp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
        
        # Ingressar no grupo multicast
        group = socket.inet_aton(UDP_MULTICAST_GROUP)
        mreq = group + socket.inet_aton('0.0.0.0')
        self.udp_socket.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
    
    def start(self):
        try:
            voter_id = input("Digite seu ID de eleitor: ")
            
            # Fazer login e obter lista de candidatos
            response = self.send_request({
                'type': 'voter',
                'voter_id': voter_id
            })
            
            # Verificação robusta da resposta
            if not response:
                print("Erro: Não houve resposta do servidor para o login")
                return
                
            if response.get('status') != 'success':
                error_msg = response.get('message', 'Erro desconhecido')
                print(f"Erro: {error_msg}")
                return
            
            # Verificar se há candidatos
            if not response.get('candidates'):
                print("Não há candidatos disponíveis. Tente novamente mais tarde.")
                return
                
            print("\nCandidatos disponíveis:")
            candidate_ids = []
            for candidate in response['candidates']:
                print(f"{candidate['id']}: {candidate['name']}")
                candidate_ids.append(candidate['id'])
            
            # Loop até receber um ID válido
            while True:
                try:
                    candidate_input = input("\nDigite o ID do candidato: ")
                    candidate_id = int(candidate_input)
                    
                    if candidate_id in candidate_ids:
                        break
                    else:
                        print("ID inválido. Escolha um ID da lista.")
                except ValueError:
                    print("Por favor, digite apenas números.")
            
            # Enviar voto
            vote_response = self.send_request({
                'candidate_id': candidate_id
            })
            
            # Verificação robusta da resposta do voto
            if not vote_response:
                print("Erro: Não houve resposta do servidor para o voto")
            elif vote_response.get('status') == 'error':
                print(f"Erro: {vote_response.get('message', 'Erro desconhecido')}")
            else:
                print(vote_response.get('message', 'Voto registrado com sucesso'))
            
            # Iniciar thread para ouvir multicast
            threading.Thread(target=self.listen_multicast, daemon=True).start()
            input("\nPressione Enter para sair...\n")
        
        except Exception as e:
            print(f"Erro: {e}")
    
    def send_request(self, data):
        """Envia uma requisição e retorna a resposta"""
        socket_conn = None
        for port in SERVER_PORTS:
            try:
                socket_conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                socket_conn.settimeout(10.0)  # Timeout maior
                socket_conn.connect((SERVER_HOST, port))
                print(f"Conectado ao servidor na porta {port}")
                break
            except (ConnectionRefusedError, socket.timeout) as e:
                print(f"Erro na porta {port}: {e}")
                if socket_conn:
                    socket_conn.close()
                socket_conn = None
            except Exception as e:
                print(f"Erro inesperado na porta {port}: {e}")
                if socket_conn:
                    socket_conn.close()
                socket_conn = None
        
        if not socket_conn:
            print("Não foi possível conectar ao servidor")
            return None
        
        try:
            print(f"Enviando dados: {data}")
            self.send_data(socket_conn, data)
            return self.receive_data(socket_conn)
        except Exception as e:
            print(f"Erro durante a operação: {e}")
            return None
        finally:
            try:
                socket_conn.close()
            except:
                pass

    def listen_multicast(self):
        print("Ouvindo notas informativas...")
        while True:
            try:
                data, _ = self.udp_socket.recvfrom(1024)
                message = json.loads(data.decode())
                if message.get('type') == 'admin_note':
                    print(f"\n[NOTA ADMIN] {message['message']}")
            except socket.timeout:
                pass
            except Exception as e:
                print(f"Erro no multicast: {e}")
    
    def send_data(self, conn, data):
        try:
            data_str = json.dumps(data)
            data_bytes = data_str.encode()
            conn.send(len(data_bytes).to_bytes(4, 'big'))
            conn.send(data_bytes)
            print(f"Dados enviados: {len(data_bytes)} bytes")
        except (BrokenPipeError, ConnectionResetError, OSError) as e:
            print(f"Erro ao enviar dados: {e}")
    
    def receive_data(self, conn):
        try:
            # Receber o tamanho dos dados
            size_bytes = conn.recv(4)
            if not size_bytes:
                print("Conexão fechada pelo servidor antes de enviar o tamanho dos dados")
                return None
            size = int.from_bytes(size_bytes, 'big')
            print(f"Esperando {size} bytes de dados...")
            
            # Receber os dados em partes
            data = b''
            while len(data) < size:
                chunk = conn.recv(min(4096, size - len(data)))
                if not chunk:
                    break
                data += chunk
            
            if len(data) < size:
                print(f"Dados incompletos. Esperados: {size} bytes, recebidos: {len(data)} bytes")
                return None
                
            print(f"Dados recebidos: {len(data)} bytes")
            return json.loads(data.decode())
        except (ConnectionResetError, json.JSONDecodeError, OSError) as e:
            print(f"Erro ao receber dados: {e}")
            return None
        except Exception as e:
            print(f"Erro inesperado ao receber dados: {e}")
            return None

if __name__ == "__main__":
    client = VoterClient()
    client.start()