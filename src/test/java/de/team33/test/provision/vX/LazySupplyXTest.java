package de.team33.test.provision.vX;

import de.team33.libs.provision.vX.LazySupply;
import de.team33.libs.provision.vX.LazySupplyX;
import org.junit.Ignore;

@Ignore
public class LazySupplyXTest extends LazySupplyTestBase {

    private final LazySupply<LazySupplyTestBase> subject = new LazySupplyX<>(this);

    @Override
    protected LazySupply<LazySupplyTestBase> getSubject() {
        return subject;
    }
}
