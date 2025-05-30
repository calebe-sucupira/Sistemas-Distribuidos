import socket
import threading
import json
import time
import sys
from datetime import datetime, timedelta

# Configurações
TCP_HOST = '0.0.0.0'
TCP_PORT_DEFAULT = 5000  # Porta padrão
UDP_MULTICAST_GROUP = '224.1.1.1'
UDP_MULTICAST_PORT = 5001
VOTING_DURATION = 120  # 2 minutos

class VotingServer:
    def __init__(self):
        self.candidates = {}  # {id: {'name': str, 'votes': int}}
        self.voters = set()   # Eleitores que já votaram
        self.voting_open = True
        self.start_time = datetime.now()
        self.end_time = self.start_time + timedelta(seconds=VOTING_DURATION)
        
        # Usar porta padrão inicialmente
        port = TCP_PORT_DEFAULT
        
        # Sockets
        self.tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.tcp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        
        try:
            self.tcp_socket.bind((TCP_HOST, port))
        except OSError as e:
            print(f"Erro ao vincular porta {port}: {e}")
            print("Tentando usar outra porta...")
            port = 5001  # Tenta uma porta alternativa
            try:
                self.tcp_socket.bind((TCP_HOST, port))
                print(f"Usando porta alternativa {port}")
            except OSError as e2:
                print(f"Erro crítico: {e2}")
                sys.exit(1)
        
        self.tcp_socket.listen(5)
        
        self.udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.udp_socket.settimeout(0.2)
        
        print(f"Servidor iniciado na porta {port}. Votação aberta até {self.end_time}")
        threading.Thread(target=self.check_voting_time, daemon=True).start()
    
    def check_voting_time(self):
        while datetime.now() < self.end_time:
            time.sleep(1)
        self.voting_open = False
        self.calculate_results()
    
    def calculate_results(self):
        total_votes = sum(c['votes'] for c in self.candidates.values())
        print("\n=== RESULTADOS FINAIS ===")
        for cid, candidate in self.candidates.items():
            percentage = (candidate['votes'] / total_votes * 100) if total_votes > 0 else 0
            print(f"{candidate['name']}: {candidate['votes']} votos ({percentage:.2f}%)")
        
        if total_votes > 0:
            winner = max(self.candidates.values(), key=lambda x: x['votes'])
            print(f"\nVENCEDOR: {winner['name']}")
    
    def handle_client(self, conn, addr):
        try:
            # Receber dados com tratamento de erro
            try:
                data = self.receive_data(conn)
            except ConnectionResetError:
                print(f"Conexão resetada por {addr}")
                return
            
            if not data:
                print(f"Conexão fechada por {addr} sem enviar dados")
                return
            
            try:
                request = json.loads(data)
            except json.JSONDecodeError as e:
                print(f"Erro ao decodificar JSON de {addr}: {e}")
                print(f"Dados recebidos: {data}")
                return
            
            # CORREÇÃO: Tratar requisições de voto
            if 'candidate_id' in request:
                voter_id = request.get('voter_id', 'desconhecido')
                candidate_id = request['candidate_id']
                
                print(f"Recebido voto para candidato {candidate_id} do eleitor {voter_id}")
                
                if not self.voting_open:
                    self.send_data(conn, {'status': 'error', 'message': 'Votação encerrada'})
                    return
                
                if candidate_id in self.candidates:
                    self.candidates[candidate_id]['votes'] += 1
                    self.voters.add(voter_id)
                    self.send_data(conn, {'status': 'success', 'message': 'Voto registrado'})
                    print(f"Voto registrado para candidato {candidate_id}")
                else:
                    self.send_data(conn, {'status': 'error', 'message': 'Candidato inválido'})
                    print(f"Candidato inválido: {candidate_id}")
            
            # Login de eleitor
            elif request.get('type') == 'voter':
                voter_id = request['voter_id']
                if voter_id in self.voters:
                    self.send_data(conn, {'status': 'error', 'message': 'Você já votou'})
                    return
                
                if not self.voting_open:
                    self.send_data(conn, {'status': 'error', 'message': 'Votação encerrada'})
                    return
                
                # Envia lista de candidatos
                candidates = [{'id': k, 'name': v['name']} for k, v in self.candidates.items()]
                self.send_data(conn, {'status': 'success', 'candidates': candidates})
                print(f"Lista de candidatos enviada para eleitor {voter_id}")
            
            # Login de administrador
            elif request.get('type') == 'admin':
                # Comandos: add_candidate, remove_candidate, send_note
                command = request.get('command')
                
                if command == 'add_candidate':
                    new_id = max(self.candidates.keys(), default=0) + 1
                    self.candidates[new_id] = {'name': request['name'], 'votes': 0}
                    self.send_data(conn, {'status': 'success', 'candidate_id': new_id})
                    print(f"Candidato adicionado: {request['name']} (ID: {new_id})")
                
                elif command == 'remove_candidate':
                    cid = request['candidate_id']
                    if cid in self.candidates:
                        del self.candidates[cid]
                        self.send_data(conn, {'status': 'success'})
                        print(f"Candidato removido: ID {cid}")
                    else:
                        self.send_data(conn, {'status': 'error', 'message': 'Candidato não encontrado'})
                
                elif command == 'send_note':
                    note = request['note']
                    self.send_multicast(note)
                    self.send_data(conn, {'status': 'success'})
                    print(f"Nota informativa enviada: {note}")
        
        except Exception as e:
            print(f"Erro com {addr}: {e}")
        finally:
            conn.close()
    
    def send_multicast(self, message):
        """Envia mensagem para grupo multicast"""
        data = json.dumps({'type': 'admin_note', 'message': message}).encode()
        self.udp_socket.sendto(data, (UDP_MULTICAST_GROUP, UDP_MULTICAST_PORT))
    
    def receive_data(self, conn):
        """Recebe dados via TCP com prefixo de tamanho"""
        try:
            size_bytes = conn.recv(4)
            if not size_bytes:
                return None
            
            size = int.from_bytes(size_bytes, 'big')
            data = b''
            while len(data) < size:
                packet = conn.recv(size - len(data))
                if not packet:
                    return None
                data += packet
                
            return data.decode()
        except (ConnectionResetError, BrokenPipeError):
            raise ConnectionResetError("Conexão resetada pelo cliente")
    
    def send_data(self, conn, data):
        """Envia dados via TCP com prefixo de tamanho"""
        try:
            data_str = json.dumps(data)
            data_bytes = data_str.encode()
            conn.send(len(data_bytes).to_bytes(4, 'big'))
            conn.send(data_bytes)
            print(f"Dados enviados para {conn.getpeername()}: {len(data_bytes)} bytes")
        except (BrokenPipeError, ConnectionResetError):
            print("Erro ao enviar dados: conexão fechada pelo cliente")
    
    def run(self):
        try:
            while True:
                conn, addr = self.tcp_socket.accept()
                print(f"Conexão estabelecida com {addr}")
                threading.Thread(target=self.handle_client, args=(conn, addr)).start()
        except KeyboardInterrupt:
            print("\nDesligando servidor...")
        finally:
            self.tcp_socket.close()
            self.udp_socket.close()

if __name__ == "__main__":
    server = VotingServer()
    server.run()