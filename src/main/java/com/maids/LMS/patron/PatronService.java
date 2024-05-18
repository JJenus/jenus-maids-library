package com.maids.LMS.patron;

import com.maids.LMS.borrowing.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
@CacheConfig(cacheNames = "patrons")
public class PatronService {

    @Autowired
    private PatronRepository patronRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    @Cacheable(key = "'allPatrons'")
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    @Cacheable(key = "#id")
    public Patron getPatronById(Long id) {
        return patronRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found"));
    }

    @Transactional
    @CachePut(key = "#result.id")
    @CacheEvict(key = "'allPatrons'")
    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    @Transactional
    @CachePut(key = "#id")
    @CacheEvict(key = "'allPatrons'")
    public Patron updatePatron(Long id, Patron updatedPatron) {
        if (patronRepository.existsById(id)) {
            updatedPatron.setId(id);
            return patronRepository.save(updatedPatron);
        }
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found");
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'allPatrons'")
    })
    public void deletePatron(Long id) {
        patronRepository.deleteById(id);
    }
}
