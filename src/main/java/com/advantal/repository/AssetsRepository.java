package com.advantal.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advantal.model.Assets;

@Repository
public interface AssetsRepository extends JpaRepository<Assets, Long> {

	List<Assets> findByWalletIdFk(Long id);

	Page<Assets> findByUpdationDateBefore(Date olderDateObj, Pageable pageable);


}
