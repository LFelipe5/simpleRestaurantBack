package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.repository.ClienteRepository;
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
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteRepository clienteRepository;

	public ClienteController(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	@GetMapping
	public List<Cliente> listar() {
		return clienteRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Cliente> buscar(@PathVariable Long id) {
		return clienteRepository.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Cliente> criar(@RequestBody ClienteRequest request) {
		String nome = normalize(request.nome());
		if (nome == null) {
			return ResponseEntity.badRequest().build();
		}

		Cliente cliente = new Cliente();
		cliente.setNome(nome);
		cliente.setEmail(normalize(request.email()));
		cliente.setTelefone(normalize(request.telefone()));

		return ResponseEntity.ok(clienteRepository.save(cliente));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody ClienteRequest request) {
		Optional<Cliente> existente = clienteRepository.findById(id);
		if (existente.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		String nome = normalize(request.nome());
		if (nome == null) {
			return ResponseEntity.badRequest().build();
		}

		Cliente cliente = existente.get();
		cliente.setNome(nome);
		cliente.setEmail(normalize(request.email()));
		cliente.setTelefone(normalize(request.telefone()));

		return ResponseEntity.ok(clienteRepository.save(cliente));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remover(@PathVariable Long id) {
		if (!clienteRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}

		clienteRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	public record ClienteRequest(String nome, String email, String telefone) {
	}
}
