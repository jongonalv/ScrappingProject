package com.example.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Jedis;


@Service
public class ScrappingServiceImp implements ScrappingService{

    @Override
    public List<Helbidea> scrap(Helbidea helbidea) {

        List<Helbidea> emaitza = new ArrayList<>();
    
        Document doc;
        try {
            doc = Jsoup.connect(helbidea.href).get();
            doc.select("a")
            .forEach(a -> emaitza.add(new Helbidea(a.attr("href"), a.html())));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        return emaitza;
    }

    @Override
    public void save(List<Helbidea> helbideak) {

        JedisPool pool = new JedisPool("172.17.0.2", 6379);

        try (Jedis jedis = pool.getResource()) {

            helbideak.forEach(h -> {
                jedis.hset(h.getHref(), h.toMap());
            });
        }
    }

    @Override
    public List<Helbidea> scrapAndSave(Helbidea helbidea) {

        List<Helbidea> H1 = scrap(helbidea);

        save(H1);

        return H1;
        
    }



    
}
