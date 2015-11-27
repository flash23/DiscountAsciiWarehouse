package asciiwarehouse.com.discountasciiwarehouse.IOService.JOBS;

/**
 * Created by mirkomesner on 11/20/15.
 */
public class SearchApiJob extends GenericJob{

    public SearchApiJob(Integer skip,Integer limit,String q,Boolean onlyInStock, Boolean reload)
    {
        if(skip!=null)
            this.skip = skip;
        if(limit!=null)
            this.limit = limit;
        if(q!=null)
            this.q = q;
        if(onlyInStock!=null)
            this.onlyInStock = onlyInStock;
        if(reload!=null)
            this.reload = reload;
    }
    public Integer skip = null;
    public Integer limit = null;
    public String q = null;
    public Boolean onlyInStock = null;

    public boolean reload = true;
}