package org.talend.dataquality.semantic;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import org.powermock.api.mockito.PowerMockito;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.provider.DefaultTenant;

public class TestUtils {

    /**
     * Method used to mock a tenant with id tenantID
     * 
     * @param tenantID
     */
    public static void mockWithTenant(String tenantID) {
        PowerMockito.mockStatic(TenancyContextHolder.class);
        TenancyContextHolder holder = mock(TenancyContextHolder.class);
        TenancyContext tenancyContext = mock(TenancyContext.class);
        DefaultTenant tenant = new DefaultTenant(tenantID, null);
        when(holder.getContext()).thenReturn(tenancyContext);
        when(tenancyContext.getTenant()).thenReturn(tenant);
    }
}
