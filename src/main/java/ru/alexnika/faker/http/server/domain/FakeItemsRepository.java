package ru.alexnika.faker.http.server.domain;

import ru.alexnika.faker.http.server.config.Config;

import java.util.*;
import java.util.stream.Collectors;

import net.datafaker.Faker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class FakeItemsRepository {
    private static final Logger logger = LogManager.getLogger(FakeItemsRepository.class.getName());
    private final List<FakeItem> fakeItems;
    private final Faker faker = new Faker(new Random(42));

    public FakeItemsRepository() {
        int initialFakeItemsQuantity = getInitialFakeItemsQuantityFromConfig();
        this.fakeItems = new ArrayList<>(initialFakeItemsQuantity);
        for (long i = 0; i < initialFakeItemsQuantity; i++) {
            FakeItem fakeItem = generateFakeItem(i, faker);
            fakeItems.add(fakeItem);
        }
    }

    public FakeItem getNewFakeItem() {
        long newId = fakeItems.stream().mapToLong(FakeItem::getId).max().orElse(0L) + 1L;
        return generateFakeItem(newId, faker);
    }

    public FakeItem add(@NotNull FakeItem fakeItem) {
        Long newId = fakeItems.stream().mapToLong(FakeItem::getId).max().orElse(0L) + 1L;
        fakeItem.setId(newId);
        fakeItems.add(fakeItem);
        logger.debug("Fake record with id '{}' added to faker repository", newId);
        return fakeItem;
    }

    public boolean delete(Long id) {
        for (FakeItem fakeItem : fakeItems) {
            if (Objects.equals(fakeItem.getId(), id)) {
                fakeItems.remove(fakeItem);
                logger.debug("Fake record with id '{}' deleted from faker repository", id);
                return true;
            }
        }
        return false;
    }

    public int getFakeItemsQuantity() {
        if (fakeItems == null || fakeItems.isEmpty()) {
            return 0;
        }
        return fakeItems.size();
    }

    public List<FakeItem> getFakeItems() {
        return Collections.unmodifiableList(fakeItems);
    }

    public List<FakeItem> getFakeItems(int fakeQuantity) {
        int beginIndex = 0;
        int endIndex = fakeQuantity - 1;
        List<FakeItem> requestedFakeItems = getFakeItemsSubList(fakeItems, beginIndex, endIndex);
        return Collections.unmodifiableList(requestedFakeItems);
    }

    public FakeItem getFakeItemById(Long id) {
        for (FakeItem fakeItem : fakeItems) {
            if (Objects.equals(fakeItem.getId(), id)) {
                return fakeItem;
            }
        }
        return null;
    }

    @Contract("_, _ -> new")
    private @NotNull FakeItem generateFakeItem(long i, @NotNull Faker faker) {
        return new FakeItem(i + 1,
                faker.name().firstName(),
                faker.name().lastName(),
                faker.address().streetAddress(),
                faker.job().position(),
                faker.hobby().activity());
    }

    private int getInitialFakeItemsQuantityFromConfig() {
        int initialFakeItemsQuantity = 10;
        try {
            initialFakeItemsQuantity = Integer.parseInt(Config.getProperty("initial.fakeItems"));
        } catch (NumberFormatException e) {
            logger.warn("Parameter 'initial.fakeItems' in file 'config.properties' must be a string consisting only of digits", e);
            logger.warn("The default 'initial.fakeItems' value is 10");
        }
        return initialFakeItemsQuantity;
    }

    private static<T> List<T> getFakeItemsSubList(@NotNull List<T> list, int startIndex, int endIndex) {
        return list.stream()
                .skip(startIndex)
                .limit(endIndex - startIndex + 1)
                .collect(Collectors.toList());
    }
}