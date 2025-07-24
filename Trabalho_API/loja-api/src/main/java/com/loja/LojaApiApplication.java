package com.loja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SpringBootApplication
@RestController
public class LojaApiApplication {

    @Autowired
    private LojaService lojaService;

    public static void main(String[] args) {
        SpringApplication.run(LojaApiApplication.class, args);
    }

    @GetMapping("/produtos")
    public List<Produto> listarProdutos() {
        return lojaService.listarTodos();
    }

    @GetMapping("/produtos/{id}")
    public ResponseEntity<Produto> buscarProduto(@PathVariable int id) {
        return lojaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/produtos")
    public Produto adicionarProduto(@RequestBody Produto produto) {
        return lojaService.adicionarProduto(produto);
    }

    @PutMapping("/produtos/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable int id, @RequestBody Produto produto) {
        Produto produtoAtualizado = lojaService.atualizarProduto(id, produto);
        if (produtoAtualizado != null) {
            return ResponseEntity.ok(produtoAtualizado); 
        }
        return ResponseEntity.notFound().build(); 
    }

    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable int id) {
        boolean deletado = lojaService.deletarProduto(id);
        if (deletado) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.notFound().build(); 
    }
}