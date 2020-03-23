package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupplyC;

public class LazySupplyCTest extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupplyC<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
