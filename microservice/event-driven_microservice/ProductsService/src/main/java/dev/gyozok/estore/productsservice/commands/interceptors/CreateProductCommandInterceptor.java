package dev.gyozok.estore.productsservice.commands.interceptors;

import dev.gyozok.estore.productsservice.commands.CreateProductCommand;
import dev.gyozok.estore.productsservice.core.data.ProductLookupEntity;
import dev.gyozok.estore.productsservice.core.data.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public @NonNull CommandMessage<?> handle(@NonNull CommandMessage<?> message) {
        return MessageDispatchInterceptor.super.handle(message);
    }

    @Override
    public @NonNull BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@NonNull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {

            LOGGER.info("Intercepted command: " + command.getPayloadType());

            if (command.getPayload() instanceof CreateProductCommand createProductCommand) {
                ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                if (productLookupEntity != null) {
                    throw new IllegalStateException(
                            String.format("Product with productId %s or title %s already exists",
                                    createProductCommand.getProductId(),
                                    createProductCommand.getTitle()
                            )
                    );
                }
            }
            return command;
        };
    }
}
