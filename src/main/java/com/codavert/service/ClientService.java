package com.codavert.service;

import com.codavert.dto.ClientDto;
import com.codavert.entity.Client;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Client createClient(ClientDto clientDto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Client client = new Client();
        // Map frontend's name to backend's companyName for convenience
        String companyName = clientDto.getCompanyName() != null ? clientDto.getCompanyName() : clientDto.getName();
        client.setCompanyName(companyName);
        client.setContactPerson(clientDto.getContactPerson());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setCity(clientDto.getCity());
        client.setState(clientDto.getState());
        client.setZipCode(clientDto.getZipCode());
        client.setCountry(clientDto.getCountry());
        client.setNotes(clientDto.getNotes());
        client.setUser(user);
        
        if (clientDto.getStatus() != null) {
            client.setStatus(Client.ClientStatus.valueOf(clientDto.getStatus()));
        }
        if (clientDto.getType() != null) {
            client.setType(Client.ClientType.valueOf(clientDto.getType()));
        }
        
        return clientRepository.save(client);
    }
    
    public Page<Client> getAllClientsByUserId(Long userId, Pageable pageable) {
        return clientRepository.findByUserId(userId, pageable);
    }
    
    public List<Client> getAllClientsByUserId(Long userId) {
        return clientRepository.findByUserId(userId);
    }
    
    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }
    
    public Client updateClient(Long id, ClientDto clientDto) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        
        String companyNameUpdate = clientDto.getCompanyName() != null ? clientDto.getCompanyName() : clientDto.getName();
        client.setCompanyName(companyNameUpdate);
        client.setContactPerson(clientDto.getContactPerson());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setCity(clientDto.getCity());
        client.setState(clientDto.getState());
        client.setZipCode(clientDto.getZipCode());
        client.setCountry(clientDto.getCountry());
        client.setNotes(clientDto.getNotes());
        
        if (clientDto.getStatus() != null) {
            client.setStatus(Client.ClientStatus.valueOf(clientDto.getStatus()));
        }
        if (clientDto.getType() != null) {
            client.setType(Client.ClientType.valueOf(clientDto.getType()));
        }
        
        return clientRepository.save(client);
    }
    
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
        
        client.setStatus(Client.ClientStatus.INACTIVE);
        clientRepository.save(client);
    }
    
    public Page<Client> searchClients(Long userId, String searchTerm, Pageable pageable) {
        return clientRepository.searchClientsByUserId(userId, searchTerm, pageable);
    }
    
    public Page<Client> getClientsByStatus(Long userId, String status, Pageable pageable) {
        return clientRepository.findByUserIdAndStatus(userId, Client.ClientStatus.valueOf(status), pageable);
    }
    
    public Page<Client> getClientsByType(Long userId, String type, Pageable pageable) {
        return clientRepository.findByUserIdAndType(userId, Client.ClientType.valueOf(type), pageable);
    }
    
    public Long getClientCountByUserId(Long userId) {
        return clientRepository.countByUserId(userId);
    }
    
    public Long getClientCountByUserIdAndStatus(Long userId, String status) {
        return clientRepository.countByUserIdAndStatus(userId, Client.ClientStatus.valueOf(status));
    }
}
