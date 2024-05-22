package com.advantal.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advantal.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

//	@Query(value = "SELECT * FROM stock st WHERE st.country =? and st.exchange=? and st.status=1 ORDER BY st.market_cap ASC", countQuery = "SELECT count(*) from stock st WHERE st.country =? and st.exchange=?  and st.status=1", nativeQuery = true)
//	Page<Stock> findAllStocks(String country, String exchange, Pageable pageable);

	@Query(value = "SELECT * FROM stock st WHERE st.price!=0 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", countQuery = "SELECT count(*) from stock st WHERE st.price!=0 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", nativeQuery = true)
	Page<Stock> findAllStocks(String keyWord, Pageable pageable);

	@Query(value = "SELECT * FROM stock st WHERE st.price!=0 and st.country=?2 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", countQuery = "SELECT count(*) from stock st WHERE st.price!=0 and st.country=?2 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", nativeQuery = true)
	Page<Stock> findAllStockByCountry(String keyWord, String country, Pageable pageable);

	Stock findByIdAndSymbol(Long instrumentId, String symbol);

	Stock findBySymbol(String symbol);

	Page<Stock> findByUpdationDateBefore(Date olderDateObj, Pageable pageable);

	Page<Stock> findByUpdationDateBeforeAndCountry(Date olderDateObj, Pageable pageable, String name);

	@Query(value = "SELECT * FROM stock st WHERE st.country =?1 and st.exchange=?2 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) from stock st WHERE st.country =?1 and st.exchange=?2  and st.status=1 and st.price!=0", nativeQuery = true)
	Page<Stock> getStockListBySorting(String country, String exchange, Pageable pageable);

	@Query(value = "SELECT * FROM stock AS st LEFT JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE sc.country =? and st.status=1 ORDER BY sc.complaince_degree DESC, sc.ticker_symbol ASC", countQuery = "SELECT count(*) from stock st WHERE st.country =? and st.status=1", nativeQuery = true)
	Page<Stock> filterBySariaComplianceWithOrderWise(String country, String exchange, Pageable pageable);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree>=1 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) from stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree>=1 and st.status=1 and st.price!=0", nativeQuery = true)
	Page<Stock> getStockListByCompliance(String country, String sector, Pageable pageable);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree>=1 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree>=1 and st.status>=1 and st.price!=0", nativeQuery = true)
	Page<Stock> getStockListByCompliance(String country, Pageable pageable);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree=0 and st.price!=0", countQuery = "SELECT count(*) from stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree=0 and st.price!=0", nativeQuery = true)
	Page<Stock> getStockListByNonCompliance(String country, String sector, Pageable pageable);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree=0 and st.price!=0", countQuery = "SELECT count(*) FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree=0 and st.price!=0", nativeQuery = true)
	Page<Stock> getStockListByNonCompliance(String country, Pageable pageable);

	@Query(value = "SELECT st.* FROM stock AS st WHERE st.country =?1 and st.sector=?2 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) from stock AS st WHERE st.country =?1 and st.sector=?2 and st.status=1 and st.price!=0", nativeQuery = true)
	Page<Stock> filterBySector(String country, String filterBySector, Pageable pageable);

	Stock findBySymbolAndUpdationDateBeforeAndCountry(String symbol, Date olderDateObj, String country);

	List<Stock> findByLastSyncDateBefore(Date olderDateObj);
//	List<Stock> findByLastSyncDateBeforeAndPrice(Date olderDateObj, Float zero);

	Page<Stock> findByLastUpdatedMarketDataBeforeAndCountry(Date olderDateObj, String country, Pageable pageable);

	@Query(value = "SELECT * FROM stock st WHERE st.country =?1 and st.status=1", countQuery = "SELECT count(*) from stock st WHERE st.country =?1 and st.status=1", nativeQuery = true)
	List<Stock> getUsaStockList(String us_country);

	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock", nativeQuery = true)
	Long findAllStockCount();

	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock st WHERE st.status=1", nativeQuery = true)
	Long findAllActiveStocksCount();

	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock st WHERE st.status=0", nativeQuery = true)
	Long findAllInactiveStocksCount();

	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock st WHERE st.country =?", nativeQuery = true)
	Long findAllKsaStocksCount(String country);

	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock st WHERE st.country =?", nativeQuery = true)
	Long findAllUsaStockCount(String country);

//	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock st WHERE st.last_sync_date between ?1 and ?2", nativeQuery = true)
	@Query(value = "SELECT COUNT(*) AS stock_count FROM stock st WHERE st.last_sync_date >=?1", nativeQuery = true)
	Long findAllSyncedStockCount(String firstDate);

	List<Stock> findByIsActivelyTrading(boolean b);

	@Query(value = "SELECT * FROM stock st WHERE st.price!=0 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", countQuery = "SELECT count(*) from stock st WHERE st.price!=0 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", nativeQuery = true)
	List<Stock> findAllStocksWithSearching(String keyWord);

	@Query(value = "SELECT * FROM stock st WHERE st.price!=0 and st.country=?2 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", countQuery = "SELECT count(*) from stock st WHERE st.price!=0 and st.country=?2 and (st.name like concat('%',?1,'%') or st.symbol like concat('%',?1,'%') or st.exchange like concat('%',?1,'%') or st.country like concat('%',?1,'%'))", nativeQuery = true)
	List<Stock> findAllStockByCountry(String keyWord, String country);

	@Query(value = "SELECT * FROM stock st WHERE st.country =?1 and st.exchange=?2 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) from stock st WHERE st.country =?1 and st.exchange=?2  and st.status=1 and st.price!=0", nativeQuery = true)
	List<Stock> getStockListBySorting(String country, String exchange);

	@Query(value = "SELECT st.* FROM stock AS st WHERE st.country =?1 and st.sector=?2 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) from stock AS st WHERE st.country =?1 and st.sector=?2 and st.status=1 and st.price!=0", nativeQuery = true)
	List<Stock> filterBySector(String country, String filterBySector);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree>=1 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) from stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree>=1 and st.status=1 and st.price!=0", nativeQuery = true)
	List<Stock> getStockListByCompliance(String country, String filterBySector);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree=0 and st.price!=0", countQuery = "SELECT count(*) from stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and st.sector=?2 and sc.complaince_degree=0 and st.price!=0", nativeQuery = true)
	List<Stock> getStockListByNonCompliance(String country, String filterBySector);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree>=1 and st.status=1 and st.price!=0", countQuery = "SELECT count(*) FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree>=1 and st.status>=1 and st.price!=0", nativeQuery = true)
	List<Stock> getStockListByCompliance(String country);

	@Query(value = "SELECT st.* FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree=0 and st.price!=0", countQuery = "SELECT count(*) FROM stock AS st INNER JOIN sharia_compliance AS sc ON sc.ticker_symbol = st.symbol WHERE st.country =?1 and sc.complaince_degree=0 and st.price!=0", nativeQuery = true)
	List<Stock> getStockListByNonCompliance(String country);

	List<Stock> findByCountryAndStatus(String country, Short status);

	

}
