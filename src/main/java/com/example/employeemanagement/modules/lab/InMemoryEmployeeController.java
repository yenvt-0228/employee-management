package com.example.employeemanagement.modules.lab;

import com.example.employeemanagement.common.util.UtilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * No login required by design (see README), so writes here must stay safe on their own:
 * basic validation plus a hard cap on the in-memory store to avoid unbounded heap growth.
 */
@RestController
@RequestMapping("/api/lab3/employees")
public class InMemoryEmployeeController {

    private static final int MAX_ITEMS = 1000;

    private final Map<Long, InMemoryEmployee> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    // Reserves a slot atomically before insert; store.size() alone would race under concurrent POSTs.
    private final AtomicInteger size = new AtomicInteger();
    private final UtilityService utilityService;

    public InMemoryEmployeeController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    /** GET /api/lab3/employees?keyword=an */
    @GetMapping
    public List<InMemoryEmployee> findAll(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(store.values());
        }
        String lower = keyword.toLowerCase();
        return store.values().stream()
                .filter(e -> e.getName() != null && e.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InMemoryEmployee> findById(@PathVariable Long id) {
        InMemoryEmployee employee = store.get(id);
        return employee == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InMemoryEmployee employee) {
        if (!StringUtils.hasText(employee.getName())) {
            return ResponseEntity.badRequest().body(Map.of("message", "name không được để trống"));
        }
        // Atomically reserve a slot: only one concurrent caller can push the count past MAX_ITEMS.
        int reserved;
        do {
            reserved = size.get();
            if (reserved >= MAX_ITEMS) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(Map.of("message", "Demo Lab 3 đã đạt giới hạn " + MAX_ITEMS + " bản ghi"));
            }
        } while (!size.compareAndSet(reserved, reserved + 1));

        long id = sequence.incrementAndGet();
        employee.setId(id);
        employee.setCode(utilityService.generateEmployeeCode(id));
        employee.setName(utilityService.normalizeName(employee.getName()));
        store.put(id, employee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(employee);
    }
}
