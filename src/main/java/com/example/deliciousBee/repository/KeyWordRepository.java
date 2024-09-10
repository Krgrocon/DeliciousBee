package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.keyWord.KeyWord;

public interface KeyWordRepository extends JpaRepository<KeyWord, Long>{

}
