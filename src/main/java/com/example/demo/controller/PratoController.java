package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.Prato;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PratoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pratos")
public class PratoController {

	private final PratoRepository pratoRepository;
	private final ClienteRepository clienteRepository;

	public PratoController(PratoRepository pratoRepository, ClienteRepository clienteRepository) {
		this.pratoRepository = pratoRepository;
		this.clienteRepository = clienteRepository;
	}

	@GetMapping
	public List<Prato> listar() {
		return pratoRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Prato> buscar(@PathVariable Long id) {
		return pratoRepository.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Prato> criar(@RequestBody PratoRequest request) {
		String nome = normalize(request.nome());
		String descricao = normalize(request.descricao());
		if (nome == null || descricao == null || request.preco() == null) {
			return ResponseEntity.badRequest().build();
		}

		Prato prato = new Prato();
		prato.setNome(nome);
		prato.setDescricao(descricao);
		prato.setPreco(request.preco());
		prato.setCliente(resolveCliente(request.clienteId()));

		return ResponseEntity.ok(pratoRepository.save(prato));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Prato> atualizar(@PathVariable Long id, @RequestBody PratoRequest request) {
		Optional<Prato> existente = pratoRepository.findById(id);
		if (existente.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		String nome = normalize(request.nome());
		String descricao = normalize(request.descricao());
		if (nome == null || descricao == null || request.preco() == null) {
			return ResponseEntity.badRequest().build();
		}

		Prato prato = existente.get();
		prato.setNome(nome);
		prato.setDescricao(descricao);
		prato.setPreco(request.preco());
		prato.setCliente(resolveCliente(request.clienteId()));

		return ResponseEntity.ok(pratoRepository.save(prato));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		if (!pratoRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		pratoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private Cliente resolveCliente(Long clienteId) {
		if (clienteId == null) {
			return null;
		}

		return clienteRepository.findById(clienteId).orElse(null);
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	public record PratoRequest(String nome, String descricao, BigDecimal preco, Long clienteId) {
	}
}
