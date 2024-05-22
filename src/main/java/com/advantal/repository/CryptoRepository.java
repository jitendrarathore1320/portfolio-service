package com.advantal.repository;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advantal.model.Crypto;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

	Crypto findByIdAndCryptoId(Long instrumentId, String cryptoId);

//	Crypto findByIdAndSymbol(Long instrumentId, String symbol);

//	Crypto findByIdAndCryptoId(Long instrumentId, String symbol);

	Crypto findByCryptoId(String cryptoId);

	@Query(value = "SELECT * FROM crypto us WHERE us.status!=0 ", countQuery = "SELECT count(*) from crypto us WHERE us.status != 0", nativeQuery = true)
	Page<Crypto> findAllCrypto(Pageable pageable);

	// published_at<= :resultDate //ORDER BY st.market_cap DESC
	@Query(value = "SELECT * FROM crypto us WHERE us.status!=0 and us.listing_time!=0 and us.listing_time>= :oneYearOldDate ORDER BY us.listing_time DESC", countQuery = "SELECT count(*) from crypto us WHERE us.status != 0 and us.listing_time!=0 and us.listing_time>= :oneYearOldDate ORDER BY us.listing_time DESC", nativeQuery = true)
	Page<Crypto> findAllNewlyListedCrypto(Pageable pageable, String oneYearOldDate);

	@Query(value = "SELECT * FROM crypto us WHERE us.status!=0 ORDER BY us.id ASC", countQuery = "SELECT count(*) from crypto us WHERE us.status != 0 ORDER BY us.id ASC", nativeQuery = true)
	List<Crypto> findAllCryptos();

	@Query(value = "SELECT COUNT(*) AS crypto_count FROM crypto", nativeQuery = true)
	Long findAllCryptosCount();

	@Query(value = "SELECT COUNT(*) AS crypto_count FROM crypto us WHERE us.status=1", nativeQuery = true)
	Long findAllActiveCryptoCount();

	@Query(value = "SELECT COUNT(*) AS crypto_count FROM crypto us WHERE us.status=0", nativeQuery = true)
	Long findAllInactiveCryptoCount();

	@Query(value = "SELECT COUNT(*) AS crypto_count FROM crypto us WHERE us.listing_time>= :oneYearOldDate", nativeQuery = true)
	Long findAllNewlyListedCryptoCount(String oneYearOldDate);

	@Query(value = "SELECT COUNT(*) AS crypto_count FROM crypto us WHERE us.updation_date >=?1", nativeQuery = true)
	Long findAllSyncedCryptoCount(String strDateTime);

	@Query(value = "SELECT * FROM crypto cry WHERE cry.status!=0 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", countQuery = "SELECT count(*) from crypto cry WHERE cry.status!=0 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", nativeQuery = true)
	Page<Crypto> findAllCryptoBySearch(String keyWord, Pageable pageable);

	@Query(value = "SELECT * FROM crypto cry WHERE cry.status!=0 and listing_time>=?2 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", countQuery = "SELECT count(*) from crypto cry WHERE cry.status!=0 and listing_time>=?2 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", nativeQuery = true)
	Page<Crypto> findAllNewlyListedCryptoBySearch(String keyWord, String oneYearOldDate, Pageable pageable);

	@Query(value = "SELECT * FROM crypto cry WHERE cry.status!=0 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", countQuery = "SELECT count(*) from crypto cry WHERE cry.status!=0 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", nativeQuery = true)
	List<Crypto> findAllCryptosWithSearching(String keyWord);

	@Query(value = "SELECT * FROM crypto cry WHERE cry.status!=0 and listing_time>=?2 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", countQuery = "SELECT count(*) from crypto cry WHERE cry.status!=0 and listing_time>=?2 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", nativeQuery = true)
	List<Crypto> findAllNewlyListedCryptosWithSearching(String keyWord,String oneYearOldDate);

	@Query(value = "SELECT * FROM crypto us WHERE us.status!=0 and us.listing_time!=0 and us.listing_time>= :oneYearOldDate ORDER BY us.listing_time DESC", countQuery = "SELECT count(*) from crypto us WHERE us.status != 0 and us.listing_time!=0 and us.listing_time>= :oneYearOldDate ORDER BY us.listing_time DESC", nativeQuery = true)
	List<Crypto> findAllNewlyListedCryptos(String oneYearOldDate);

	@Query(value = "SELECT * FROM crypto us WHERE us.status!=0 ORDER BY us.id ASC", countQuery = "SELECT count(*) from crypto us WHERE us.status != 0 ORDER BY us.id ASC", nativeQuery = true)
	List<Crypto> findAllCryptosWithoutPagination();

	@Query(value = "SELECT * FROM crypto cry WHERE cry.status!=0 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", countQuery = "SELECT count(*) from crypto cry WHERE cry.status!=0 and (cry.name like concat('%',?1,'%') or cry.symbol like concat('%',?1,'%'))", nativeQuery = true)
	List<Crypto> findAllCryptosWithoutPaginationWithSearching(String keyWord);

}
