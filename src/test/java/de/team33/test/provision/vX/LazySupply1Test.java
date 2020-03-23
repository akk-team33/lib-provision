package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupply1;

public class LazySupply1Test extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupply1<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
