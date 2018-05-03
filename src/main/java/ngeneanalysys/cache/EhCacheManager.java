package ngeneanalysys.cache;

import net.sf.ehcache.CacheManager;
import ngeneanalysys.code.constants.CommonConstants;
import ngeneanalysys.util.ResourceUtil;

import java.net.URL;

/**
 * @author Jang
 * @since 2017-08-08
 */
public class EhCacheManager {

    private CacheManager cacheManager = null;

    private EhCacheManager() {
        ResourceUtil resourceUtil = new ResourceUtil();
        URL configFile = resourceUtil.getResourceURL(CommonConstants.EHCACHE_PATH);
        cacheManager = new CacheManager(configFile);
    }
    /**
     * 싱글톤 인스턴스 생성 내부 클래스
     */
    private static class EhCacheManagerHelper{
        private EhCacheManagerHelper() {}
        private static final EhCacheManager INSTANCE = new EhCacheManager();
    }

    /**
     * 싱글톤 객체 반환
     * @return
     */
    public static EhCacheManager getInstance() {
        return EhCacheManagerHelper.INSTANCE;
    }


}
