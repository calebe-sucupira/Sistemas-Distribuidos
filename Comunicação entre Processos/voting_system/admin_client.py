import socket
import json
import sys

SERVER_HOST = 'localhost'
SERVER_PORTS = [5000, 5001] 

class AdminClient:
    def __init__(self):
        pass  
    
    def start(self):
        print("=== PAINEL ADMINISTRATIVO ===")
        while True:
            print("\nOpções:")
            print("1. Adicionar candidato")
            print("2. Remover candidato")
            print("3. Enviar nota informativa")
            print("4. Sair")
            
            choice = input("Escolha: ")
            
            if choice == '1':
                name = input("Nome do candidato: ")
                self.send_command('add_candidate', {'name': name})
            
            elif choice == '2':
                cid = int(input("ID do candidato: "))
                self.send_command('remove_candidate', {'candidate_id': cid})
            
            elif choice == '3':
                note = input("Mensagem: ")
                self.send_command('send_note', {'note': note})
            
            elif choice == '4':
                break
    
    def send_command(self, command, data):
        # Tentar conectar em ambas as portas
        socket_conn = None
        for port in SERVER_PORTS:
            try:
                socket_conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                socket_conn.connect((SERVER_HOST, port))
                print(f"Conectado na porta {port}")
                break
            except ConnectionRefusedError:
                print(f"Porta {port} recusada, tentando próxima...")
                socket_conn = None
            except Exception as e:
                print(f"Erro na porta {port}: {e}")
                socket_conn = None
        
        if not socket_conn:
            print("Não foi possível conectar ao servidor")
            return
        
        try:
            request = {
                'type': 'admin',
                'command': command,
                **data
            }
            self.send_data(socket_conn, request)
            response = self.receive_data(socket_conn)
            print(response.get('message', 'Operação realizada com sucesso'))
        except Exception as e:
            print(f"Erro durante a operação: {e}")
        finally:
            socket_conn.close()
    
    def send_data(self, conn, data):
        data_str = json.dumps(data)
        data_bytes = data_str.encode()
        conn.send(len(data_bytes).to_bytes(4, 'big'))
        conn.send(data_bytes)
    
    def receive_data(self, conn):
        size_bytes = conn.recv(4)
        if not size_bytes:
            return {}
        size = int.from_bytes(size_bytes, 'big')
        data = conn.recv(size)
        return json.loads(data.decode())

if __name__ == "__main__":
    admin = AdminClient()
    admin.start()