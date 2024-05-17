package com.maids.LMS.patron;

import com.maids.LMS.borrowing.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    @Cacheable
    public Patron getPatronById(Long id) {
        return patronRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found"));
    }

    @Transactional
    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    @Transactional
    public Patron updatePatron(Long id, Patron updatedPatron) {
        if (patronRepository.existsById(id)) {
            updatedPatron.setId(id);
            return patronRepository.save(updatedPatron);
        }
        throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found");
    }

    @Transactional
    public void deletePatron(Long id) {
        patronRepository.findById(id).ifPresent(book -> borrowingRepository.deleteByPatron(book));
        patronRepository.deleteById(id);
    }
}
