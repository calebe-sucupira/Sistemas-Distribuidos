import requests
import json

API_URL = "http://localhost:8080"

def listar_produtos():
    try:
        response = requests.get(f"{API_URL}/produtos")
        if response.status_code == 200:
            produtos = response.json()
            print("\n--- Catálogo de Produtos da Loja ---")
            if not produtos:
                print("Nenhum produto no estoque.")
            else:
                for produto in produtos:
                    print(
                        f"  ID: {produto['id']} | "
                        f"Nome: {produto['nome']} | "
                        f"Marca: {produto['marca']} | "
                        f"Preço: R$ {produto['preco']:.2f}"
                    )
            print("--------------------------------------\n")
        else:
            print(f"Erro ao buscar produtos. Código: {response.status_code}")
    except requests.exceptions.ConnectionError:
        print("\n[ERRO CRÍTICO] Falha na conexão com a API.")

def adicionar_produto():
    print("\n--- Adicionar Novo Produto ---")
    try:
        nome = input("Digite o nome: ")
        marca = input("Digite a marca: ")
        preco = float(input("Digite o preço: "))
        novo_produto = {"nome": nome, "marca": marca, "preco": preco}
        response = requests.post(f"{API_URL}/produtos", json=novo_produto)
        if response.status_code == 200 or response.status_code == 201:
            print("\nProduto adicionado com sucesso!")
        else:
            print(f"\nErro ao adicionar produto. Código: {response.status_code}")
    except ValueError:
        print("\nErro: Preço deve ser um número.")
    except requests.exceptions.ConnectionError:
        print("\n[ERRO CRÍTICO] Falha na conexão com a API.")

def atualizar_produto():
    print("\n--- Atualizar Produto ---")
    try:
        id = int(input("Digite o ID do produto a ser atualizado: "))
        nome = input("Digite o novo nome: ")
        marca = input("Digite a nova marca: ")
        preco = float(input("Digite o novo preço: "))
        produto_atualizado = {"nome": nome, "marca": marca, "preco": preco}
        response = requests.put(f"{API_URL}/produtos/{id}", json=produto_atualizado)
        if response.status_code == 200:
            print("\nProduto atualizado com sucesso!")
        elif response.status_code == 404:
            print("\nErro: Produto com este ID não foi encontrado.")
        else:
            print(f"\nErro ao atualizar produto. Código: {response.status_code}")
    except ValueError:
        print("\nErro: ID e Preço devem ser números.")
    except requests.exceptions.ConnectionError:
        print("\n[ERRO CRÍTICO] Falha na conexão com a API.")

def deletar_produto():
    print("\n--- Deletar Produto ---")
    try:
        id = int(input("Digite o ID do produto a ser deletado: "))
        response = requests.delete(f"{API_URL}/produtos/{id}")
        if response.status_code == 204:
            print("\nProduto deletado com sucesso!")
        elif response.status_code == 404:
            print("\nErro: Produto com este ID não foi encontrado.")
        else:
            print(f"\nErro ao deletar produto. Código: {response.status_code}")
    except ValueError:
        print("\nErro: ID deve ser um número.")
    except requests.exceptions.ConnectionError:
        print("\n[ERRO CRÍTICO] Falha na conexão com a API.")


def menu():
    while True:
        print("\n--- Menu Cliente da Loja ---")
        print("1. Listar todos os produtos")
        print("2. Adicionar um novo produto")
        print("3. Atualizar um produto")
        print("4. Deletar um produto")
        print("5. Sair")
        escolha = input("Digite sua escolha: ")

        if escolha == '1':
            listar_produtos()
        elif escolha == '2':
            adicionar_produto()
        elif escolha == '3':
            atualizar_produto()
        elif escolha == '4':
            deletar_produto()
        elif escolha == '5':
            print("Saindo...")
            break
        else:
            print("Opção inválida, por favor tente novamente.")

if __name__ == "__main__":
    menu()