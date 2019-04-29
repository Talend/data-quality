package org.talend.dataquality.semantic;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import java.util.Optional;

import org.powermock.api.mockito.PowerMockito;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.daikon.multitenant.provider.DefaultTenant;

public class TestUtils {

    /**
     * Method used to mock a tenant with id tenantID
     * 
     * @param tenantID
     */
    public static void mockWithTenant(String tenantID) {
        TenancyContext tenancyContext = mock(TenancyContext.class);
        when(tenancyContext.getOptionalTenant()).thenReturn(Optional.of(new DefaultTenant(tenantID, null)));

        PowerMockito.mockStatic(TenancyContextHolder.class);
        when(TenancyContextHolder.getContext()).thenReturn(tenancyContext);
    }
}
