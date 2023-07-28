package com.example.demo.Services;

import com.example.demo.Objects.Opinion;
import com.example.demo.Repositories.OpinionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpinionService {

    @Autowired
    OpinionRepository repository;

    public Optional<Opinion> searchOpinion(Long id) {

        return repository.findById(id);
    }

    public List<Opinion> searchAllOpinions() {

        return repository.findAll();
    }

    public Opinion saveOpinion(Opinion opinion) {

        return repository.save(opinion);
    }
}
