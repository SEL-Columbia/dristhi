package org.opensrp.register.repository;

import org.opensrp.common.AllConstants;
import org.opensrp.register.domain.Child;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.register.domain.Mother;
import org.opensrp.service.formSubmission.ziggy.EntityDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FormZiggyMapper {
	private EntityDataMap edMap;

    @Autowired
    public FormZiggyMapper(EntityDataMap edMap) {
        this.edMap = edMap;
        initMaps();
    }

    private void initMaps() {
    	System.out.println("Adding Custom entities to EntityDataMap (ZiggyService)");
        edMap.addEntity(AllConstants.FormEntityTypes.ELIGIBLE_COUPLE_TYPE, EligibleCouple.class);
        edMap.addEntity(AllConstants.FormEntityTypes.MOTHER_TYPE, Mother.class);
        edMap.addEntity(AllConstants.FormEntityTypes.CHILD_TYPE, Child.class);
    }
}
