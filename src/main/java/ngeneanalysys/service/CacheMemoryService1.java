package ngeneanalysys.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import ngeneanalysys.util.LoggerUtil;
import org.slf4j.Logger;

/**
 * @author Jang
 * @since 2017-08-04
 */
public class CacheMemoryService1 {
    private final static Logger logger = LoggerUtil.getLogger();

    private Ehcache ehCache;

    /**
     * 인스턴스 생성 제한
     */
    private CacheMemoryService1() {

    }

    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class CacheMemoryServiceHelper{
        private CacheMemoryServiceHelper() {}
        private static final CacheMemoryService1 INSTANCE = new CacheMemoryService1();
    }

    /**
     * 싱글톤 객체 반환
     * @return
     */
    public static CacheMemoryService1 getInstance() {
        return CacheMemoryServiceHelper.INSTANCE;
    }

    /**
     *  캐쉬 세팅 객체 반환
     * @param cacheSetName
     * @return
     */
    public Cache getCache(String cacheSetName) {
        Cache cache = ehCache.getCacheManager().getCache(cacheSetName);
        return cache;
    }

    public boolean isEmpty(String cacheSetName, String cacheKeyName) {
        Cache cache = getCache(cacheSetName);
        if(cache != null) {
            if(cache.get(cacheKeyName) != null) {
                return false;
            } else {
                logger.info("cache [" + cacheSetName + "]'s data [" + cacheKeyName + "] is null");
            }
        } else {
            logger.info("cache [" + cacheSetName + "] is null");
        }
        return true;
    }

    /**
     *  캐시 메모리 삽입
     * @param cacheSetName
     * @param cacheKeyName
     * @param obj
     */
    public void setCacheObject(String cacheSetName, String cacheKeyName, Object obj) {
        try {
            logger.info(String.format("Set Cache [set : %s, key : %s]", cacheSetName, cacheKeyName));
            Cache cache = getCache(cacheSetName);
            cache.put(new Element(cacheKeyName, obj));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *  캐시 메모리 데이터 반환
     * @param cacheSetName
     * @param cacheKeyName
     * @return
     */
    public Object getCacheObject(String cacheSetName, String cacheKeyName) {
        Object obj = null;

        Cache cache = getCache(cacheSetName);
        if(cache != null) {
            Element value = cache.get(cacheKeyName);
            if(value != null) {
                obj = value.getObjectValue();
            } else {
                logger.info("[getCacheObject] cache [" + cacheSetName + "]'s data [" + cacheKeyName + "] is null");
            }
        } else {
            logger.info("[getCacheObject] cache [" + cacheSetName + "] is null");
        }
        return obj;
    }

    public void removeCacheKey(String cacheSetName, String cacheKeyName) {
        try {
            logger.info(String.format("Remove Cache [set : %s, key : %s]", cacheSetName, cacheKeyName));
            Cache cache = getCache(cacheSetName);
            cache.remove(cacheKeyName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
