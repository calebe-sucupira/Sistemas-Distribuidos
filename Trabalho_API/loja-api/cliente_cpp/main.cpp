#include <iostream>
#include <string>
#include <vector>
#include <iomanip>
#include <limits>
#include "httplib.h"
#include "json.hpp"

using json = nlohmann::json;

const char* host = "localhost";
const int port = 8080;

void listarProdutos() {
    httplib::Client cli(host, port);
    cli.set_connection_timeout(5);
    auto res = cli.Get("/produtos");

    std::cout << "\n--- Catálogo de Produtos da Loja ---" << std::endl;
    if (res && res->status == 200) {
        try {
            json produtos = json::parse(res->body);
            if (produtos.empty()) {
                std::cout << "Nenhum produto no estoque." << std::endl;
            } else {
                for (const auto& produto : produtos) {
                    std::cout << "  ID: " << produto["id"].get<int>()
                              << " | Nome: " << produto["nome"].get<std::string>()
                              << " | Marca: " << produto["marca"].get<std::string>()
                              << " | Preço: R$ " << std::fixed << std::setprecision(2) << produto["preco"].get<double>()
                              << std::endl;
                }
            }
        } catch (json::parse_error& e) {
            std::cerr << "[ERRO] Falha ao analisar a resposta do servidor: " << e.what() << std::endl;
        }
    } else {
        std::cout << "[ERRO] Não foi possível buscar os produtos." << std::endl;
    }
    std::cout << "--------------------------------------\n" << std::endl;
}

void adicionarProduto() {
    httplib::Client cli(host, port);
    cli.set_connection_timeout(5);
    std::string nome, marca;
    double preco;

    std::cout << "\n--- Adicionar Novo Produto ---" << std::endl;
    std::cout << "Digite o nome: ";
    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    std::getline(std::cin, nome);
    std::cout << "Digite a marca: ";
    std::getline(std::cin, marca);
    std::cout << "Digite o preço: ";
    std::cin >> preco;

    json novo_produto = {
        {"nome", nome},
        {"marca", marca},
        {"preco", preco}
    };
    std::string json_body = novo_produto.dump();
    auto res = cli.Post("/produtos", json_body, "application/json");

    if (res && (res->status == 200 || res->status == 201)) {
        std::cout << "\nProduto adicionado com sucesso!" << std::endl;
        std::cout << "Dados recebidos do servidor: " << res->body << std::endl;
    } else {
        auto err_msg = res ? "Código " + std::to_string(res->status) : "Falha na conexão";
        std::cout << "\n[ERRO] Não foi possível adicionar o produto. Causa: " << err_msg << std::endl;
    }
}

void atualizarProduto() {
    httplib::Client cli(host, port);
    cli.set_connection_timeout(5);
    int id;
    std::string nome, marca;
    double preco;

    std::cout << "\n--- Atualizar Produto ---" << std::endl;
    std::cout << "Digite o ID do produto a ser atualizado: ";
    std::cin >> id;

    std::cout << "Digite o NOVO nome: ";
    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    std::getline(std::cin, nome);
    std::cout << "Digite a NOVA marca: ";
    std::getline(std::cin, marca);
    std::cout << "Digite o NOVO preço: ";
    std::cin >> preco;

    json produto_atualizado = {
        {"id", id},
        {"nome", nome},
        {"marca", marca},
        {"preco", preco}
    };
    std::string json_body = produto_atualizado.dump();
    std::string path = "/produtos/" + std::to_string(id);

    auto res = cli.Put(path.c_str(), json_body, "application/json");

    if (res && res->status == 200) {
        std::cout << "\nProduto atualizado com sucesso!" << std::endl;
    } else if (res && res->status == 404) {
        std::cout << "\nErro: Produto com ID " << id << " não foi encontrado." << std::endl;
    } else {
        auto err_msg = res ? "Código " + std::to_string(res->status) : "Falha na conexão";
        std::cout << "\n[ERRO] Não foi possível atualizar o produto. Causa: " << err_msg << std::endl;
    }
}

void deletarProduto() {
    httplib::Client cli(host, port);
    cli.set_connection_timeout(5);
    int id;
    
    std::cout << "\n--- Deletar Produto ---" << std::endl;
    std::cout << "Digite o ID do produto a ser deletado: ";
    std::cin >> id;

    std::string path = "/produtos/" + std::to_string(id);

    auto res = cli.Delete(path.c_str());

    if (res && res->status == 204) { 
        std::cout << "\nProduto deletado com sucesso!" << std::endl;
    } else if (res && res->status == 404) {
        std::cout << "\nErro: Produto com ID " << id << " não foi encontrado." << std::endl;
    } else {
        auto err_msg = res ? "Código " + std::to_string(res->status) : "Falha na conexão";
        std::cout << "\n[ERRO] Não foi possível deletar o produto. Causa: " << err_msg << std::endl;
    }
}

void exibirMenu() {
    std::cout << "\n--- Menu Cliente da Loja (C++) ---" << std::endl;
    std::cout << "1. Listar todos os produtos" << std::endl;
    std::cout << "2. Adicionar um novo produto" << std::endl;
    std::cout << "3. Atualizar um produto" << std::endl;
    std::cout << "4. Deletar um produto" << std::endl;
    std::cout << "5. Sair" << std::endl;
    std::cout << "Digite sua escolha: ";
}

int main() {
    std::string escolha;
    while (true) {
        exibirMenu();
        std::cin >> escolha;

        if (escolha == "1") {
            listarProdutos();
        } else if (escolha == "2") {
            adicionarProduto();
        } else if (escolha == "3") {
            atualizarProduto();
        } else if (escolha == "4") {
            deletarProduto();
        } else if (escolha == "5") {
            std::cout << "Saindo..." << std::endl;
            break;
        } else {
            std::cout << "Opção inválida, por favor tente novamente." << std::endl;
        }
    }
    return 0;
}