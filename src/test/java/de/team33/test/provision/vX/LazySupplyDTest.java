package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupplyD;

public class LazySupplyDTest extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupplyD<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
