package com.codavert.service;

import com.codavert.dto.ClientDetailDto;
import com.codavert.dto.ClientDto;
import com.codavert.dto.InvoiceDto;
import com.codavert.dto.ProjectDocumentDto;
import com.codavert.dto.ProjectDto;
import com.codavert.entity.Client;
import com.codavert.entity.Invoice;
import com.codavert.entity.Project;
import com.codavert.entity.ProjectDocument;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.InvoiceRepository;
import com.codavert.repository.ProjectDocumentRepository;
import com.codavert.repository.ProjectRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private ProjectDocumentRepository documentRepository;
    
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
    
    public ClientDetailDto getClientDetails(Long clientId, Long userId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));
        
        // Verify ownership
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Client does not belong to user");
        }
        
        // Get related entities
        List<Project> projects = projectRepository.findByClientId(clientId);
        List<Invoice> invoices = invoiceRepository.findByClientId(clientId);
        List<ProjectDocument> documents = documentRepository.findByClientIdAndUserId(clientId, userId);
        
        // Calculate statistics
        ClientDetailDto.ClientStatisticsDto statistics = new ClientDetailDto.ClientStatisticsDto();
        statistics.setTotalProjects((long) projects.size());
        statistics.setActiveProjects(projects.stream()
            .filter(p -> p.getStatus() == Project.ProjectStatus.IN_PROGRESS)
            .count());
        statistics.setCompletedProjects(projects.stream()
            .filter(p -> p.getStatus() == Project.ProjectStatus.COMPLETED)
            .count());
        
        statistics.setTotalInvoices((long) invoices.size());
        statistics.setPaidInvoices(invoices.stream()
            .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PAID)
            .count());
        statistics.setPendingInvoices(invoices.stream()
            .filter(i -> i.getStatus() == Invoice.InvoiceStatus.SENT || 
                         i.getStatus() == Invoice.InvoiceStatus.VIEWED ||
                         i.getStatus() == Invoice.InvoiceStatus.DRAFT)
            .count());
        statistics.setOverdueInvoices(invoices.stream()
            .filter(i -> i.getDueDate() != null && 
                         i.getDueDate().isBefore(LocalDate.now()) &&
                         i.getStatus() != Invoice.InvoiceStatus.PAID)
            .count());
        
        statistics.setTotalRevenue(invoices.stream()
            .filter(i -> i.getStatus() == Invoice.InvoiceStatus.PAID && i.getTotalAmount() != null)
            .map(Invoice::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        statistics.setPendingRevenue(invoices.stream()
            .filter(i -> i.getStatus() != Invoice.InvoiceStatus.PAID && i.getTotalAmount() != null)
            .map(Invoice::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        statistics.setTotalDocuments((long) documents.size());
        
        // Map to DTOs
        ClientDto clientDto = mapToClientDto(client);
        List<ProjectDto> projectDtos = projects.stream()
            .map(this::mapToProjectDto)
            .collect(Collectors.toList());
        List<InvoiceDto> invoiceDtos = invoices.stream()
            .map(this::mapToInvoiceDto)
            .collect(Collectors.toList());
        List<ProjectDocumentDto> documentDtos = documents.stream()
            .map(this::mapToDocumentDto)
            .collect(Collectors.toList());
        
        ClientDetailDto detailDto = new ClientDetailDto();
        detailDto.setClient(clientDto);
        detailDto.setStatistics(statistics);
        detailDto.setProjects(projectDtos);
        detailDto.setInvoices(invoiceDtos);
        detailDto.setDocuments(documentDtos);
        
        return detailDto;
    }
    
    private ClientDto mapToClientDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setCompanyName(client.getCompanyName());
        dto.setName(client.getCompanyName()); // Alias for frontend
        dto.setContactPerson(client.getContactPerson());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());
        dto.setAddress(client.getAddress());
        dto.setCity(client.getCity());
        dto.setState(client.getState());
        dto.setZipCode(client.getZipCode());
        dto.setCountry(client.getCountry());
        dto.setNotes(client.getNotes());
        dto.setStatus(client.getStatus() != null ? client.getStatus().name() : null);
        dto.setType(client.getType() != null ? client.getType().name() : null);
        return dto;
    }
    
    private ProjectDto mapToProjectDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setName(project.getTitle()); // Alias for frontend
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus() != null ? project.getStatus().name() : null);
        dto.setType(project.getType() != null ? project.getType().name() : null);
        dto.setPriority(project.getPriority() != null ? project.getPriority().name() : null);
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setEstimatedHours(project.getEstimatedHours());
        dto.setActualHours(project.getActualHours());
        dto.setBudget(project.getBudget());
        dto.setHourlyRate(project.getHourlyRate());
        dto.setRequirements(project.getRequirements());
        dto.setDeliverables(project.getDeliverables());
        dto.setTechnologies(project.getTechnologies());
        dto.setClientId(project.getClient() != null ? project.getClient().getId() : null);
        dto.setClientName(project.getClient() != null ? project.getClient().getCompanyName() : null);
        return dto;
    }
    
    private InvoiceDto mapToInvoiceDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setNotes(invoice.getNotes());
        dto.setPaymentTerms(invoice.getPaymentTerms());
        dto.setPaidDate(invoice.getPaidDate());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);
        dto.setClientId(invoice.getClient() != null ? invoice.getClient().getId() : null);
        dto.setProjectId(invoice.getProject() != null ? invoice.getProject().getId() : null);
        dto.setUserId(invoice.getUser() != null ? invoice.getUser().getId() : null);
        return dto;
    }
    
    private ProjectDocumentDto mapToDocumentDto(ProjectDocument document) {
        ProjectDocumentDto dto = new ProjectDocumentDto();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setDescription(document.getDescription());
        dto.setType(document.getType() != null ? document.getType().name() : null);
        dto.setStatus(document.getStatus() != null ? document.getStatus().name() : null);
        dto.setFilePath(document.getFilePath());
        dto.setFileName(document.getFileName());
        dto.setFileType(document.getFileType());
        dto.setFileSize(document.getFileSize());
        dto.setContent(document.getContent());
        dto.setTemplate(document.getTemplate());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setProjectId(document.getProject() != null ? document.getProject().getId() : null);
        dto.setProjectTitle(document.getProject() != null ? document.getProject().getTitle() : null);
        dto.setUserId(document.getUser() != null ? document.getUser().getId() : null);
        return dto;
    }
}
