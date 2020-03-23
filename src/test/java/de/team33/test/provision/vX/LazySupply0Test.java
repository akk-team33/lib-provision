package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupply0;

public class LazySupply0Test extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupply0<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
