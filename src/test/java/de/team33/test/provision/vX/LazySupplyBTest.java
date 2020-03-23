package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupplyB;

public class LazySupplyBTest extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupplyB<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
