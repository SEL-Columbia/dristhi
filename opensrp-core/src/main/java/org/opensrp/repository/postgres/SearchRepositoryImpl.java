package org.opensrp.repository.postgres;

import java.util.List;

import org.opensrp.domain.Client;
import org.opensrp.repository.SearchRepository;
import org.opensrp.search.ClientSearchBean;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl implements SearchRepository {
	
	@Override
	public List<Client> findByCriteria(ClientSearchBean clientSearchBean, String firstName, String middleName,
	                                   String lastName, Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
