package com.shopsphere.config;

import com.shopsphere.entity.Product;
import com.shopsphere.entity.Role;
import com.shopsphere.entity.User;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

/** Seeds an admin account and sample products on first run. */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@shopsphere.com")) {
            userRepository.save(User.builder().name("Admin").email("admin@shopsphere.com")
                    .password(passwordEncoder.encode("Admin@123")).role(Role.ADMIN).build());
        }
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                p("Mechanical Keyboard", "Hot-swappable 75% keyboard with tactile switches", "3499.00", 25, "Accessories"),
                p("Wireless Mouse", "Ergonomic mouse with 2.4GHz receiver", "799.00", 60, "Accessories"),
                p("27-inch Monitor", "QHD IPS display, 144Hz", "18999.00", 12, "Displays"),
                p("USB-C Hub", "7-in-1 hub with HDMI and card reader", "1599.00", 40, "Accessories"),
                p("Laptop Stand", "Aluminium adjustable stand", "1199.00", 35, "Accessories"),
                p("Noise-Cancelling Headphones", "Over-ear Bluetooth headphones, 30h battery", "7999.00", 18, "Audio"),
                p("Bluetooth Speaker", "Portable waterproof speaker", "2499.00", 30, "Audio"),
                p("Webcam 1080p", "Full HD webcam with built-in mic", "2199.00", 22, "Displays")));
        }
    }

    private Product p(String name, String desc, String price, int stock, String category) {
        return Product.builder().name(name).description(desc).price(new BigDecimal(price))
                .stock(stock).category(category)
                .imageUrl("https://picsum.photos/seed/" + name.replace(' ', '-') + "/400/300").build();
    }
}
