package com.example.deliciousBee.repository;

import com.example.deliciousBee.model.review.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
