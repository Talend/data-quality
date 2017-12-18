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
        PowerMockito.mockStatic(TenancyContextHolder.class);
        TenancyContextHolder holder = mock(TenancyContextHolder.class);
        TenancyContext tenancyContext = mock(TenancyContext.class);
        Optional<Tenant> optionalTenant = Optional.of(new DefaultTenant(tenantID, null));
        when(holder.getContext()).thenReturn(tenancyContext);
        when(tenancyContext.getOptionalTenant()).thenReturn(optionalTenant);
    }
}
