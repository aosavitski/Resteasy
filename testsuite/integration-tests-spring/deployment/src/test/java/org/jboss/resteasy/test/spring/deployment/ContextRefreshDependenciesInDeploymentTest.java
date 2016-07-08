package org.jboss.resteasy.test.spring.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.spring.deployment.resource.ContextRefreshResource;
import org.jboss.resteasy.test.spring.deployment.resource.ContextRefreshTrigger;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.context.WebApplicationContext;
import java.util.Enumeration;


/**
 * @tpSubChapter Spring
 * @tpChapter Integration tests - dependencies included in deployment
 * @tpTestCaseDetails Spring context refresh, RESTEASY-632
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
public class ContextRefreshDependenciesInDeploymentTest {


    private static Logger logger = Logger.getLogger(ContextRefreshDependenciesInDeploymentTest.class);

    @Deployment
    private static Archive<?> deploy() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, ContextRefreshDependenciesInDeploymentTest.class.getSimpleName() + ".war")
                .addClass(ContextRefreshResource.class)
                .addClass(ContextRefreshTrigger.class)
                .addClass(ContextRefreshDependenciesInDeploymentTest.class)
                .addAsWebInfResource(ContextRefreshDependenciesInDeploymentTest.class.getPackage(), "web.xml", "web.xml")
                .addAsWebInfResource(ContextRefreshDependenciesInDeploymentTest.class.getPackage(), "contextRefresh/applicationContext.xml", "applicationContext.xml");
        TestUtilSpring.addSpringLibraries(archive);
        return archive;
    }

    /**
     * @tpTestDetails Refresh the persistent representation of the spring configuration twice
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testContextRefresh() throws Exception {
        Assert.assertTrue(ContextRefreshTrigger.isOK());
        Enumeration<?> en = ContextRefreshTrigger.getApplicationContext().getServletContext().getAttributeNames();
        while (en.hasMoreElements()) {
            logger.info(en.nextElement());
        }
        Object o = ContextRefreshTrigger.getApplicationContext().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        logger.info(o);
        Assert.assertFalse(o instanceof Exception);
    }
}
