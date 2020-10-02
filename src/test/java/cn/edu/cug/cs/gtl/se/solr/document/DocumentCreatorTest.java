package cn.edu.cug.cs.gtl.se.solr.document;

import cn.edu.cug.cs.gtl.config.Config;
import cn.edu.cug.cs.gtl.filter.FileFilter;
import cn.edu.cug.cs.gtl.protoswrapper.DocumentMapperWrapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DocumentCreatorTest {

    static String rawDir = Config.getTestInputDirectory()+ cn.edu.cug.cs.gtl.io.File.separator+"lucene"+ cn.edu.cug.cs.gtl.io.File.separator
            +"dat"+ cn.edu.cug.cs.gtl.io.File.separator+"raw";

    static String shpDir = Config.getTestInputDirectory()+ cn.edu.cug.cs.gtl.io.File.separator+"lucene"+ cn.edu.cug.cs.gtl.io.File.separator
            +"dat"+ cn.edu.cug.cs.gtl.io.File.separator+"shp";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void of() throws Exception {
        List<SolrInputDocument> docs = DocumentCreator
                .of(rawDir,
                        FileFilter.textsFileFilter(),
                        DocumentMapperWrapper.fileMapper())
                .execute();


    }

    @Test
    public void execute() throws Exception{
        String serverURL = "http://120.24.168.173:8983/solr";
        SolrClient solrClient = new HttpSolrClient
                .Builder()
                .withBaseSolrUrl(serverURL)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();

        try {
            //由于没有在solr服务器端配置空间数据的字段，测试不通过
            //https://lucene.apache.org/solr/guide/8_3/spatial-search.html
            String rawDir = Config.getTestInputDirectory()+ cn.edu.cug.cs.gtl.io.File.separator
                    +"lucene"+ cn.edu.cug.cs.gtl.io.File.separator
                    +"dat"+ cn.edu.cug.cs.gtl.io.File.separator
                    +"raw"+ cn.edu.cug.cs.gtl.io.File.separator
                    +"shape";
            List<SolrInputDocument> docs = DocumentCreator
                    .of(rawDir, FileFilter.shapesFileFilter(), DocumentMapperWrapper.paragraphMapper())
                    .execute();

            for(SolrInputDocument s: docs){
                final UpdateResponse updateResponse = solrClient.add("gtl", s);
                // Indexed documents must be committed
                solrClient.commit("gtl");
                System.out.println(updateResponse.jsonStr());
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}