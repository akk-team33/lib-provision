package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupplyA;

public class LazySupplyATest extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupplyA<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
