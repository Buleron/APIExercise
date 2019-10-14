import play.filters.cors.CORSFilter;
import play.filters.hosts.AllowedHostsFilter;
import play.http.DefaultHttpFilters;

import javax.inject.Inject;

public class Filters extends DefaultHttpFilters {
	
	@Inject public Filters(CORSFilter corsFilter, AllowedHostsFilter hostsFilter) {
        super(corsFilter, hostsFilter);
    }
}
