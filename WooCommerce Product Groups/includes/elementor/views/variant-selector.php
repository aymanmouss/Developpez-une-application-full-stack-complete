<?php if (!defined('ABSPATH')) exit; ?>

<div class="product-variants-container">
    <div class="variant-selector-header">
        <div class="header-text">Couleur et capacité</div>
        <div class="header-price">Prix</div>
    </div>
    
    <div class="variant-options">
        <?php foreach ($products_in_group as $variant): 
            $is_current = $variant['id'] === $product->get_id();
            $stock_class = $variant['stock_status'] === 'instock' ? 'in-stock' : 'out-of-stock';
            $stock_text = $variant['stock_status'] === 'instock' ? 'En stock' : '5 à 7 jours ouvrables';
            
            // Get color and capacity from attributes
            $color = isset($variant['attributes']['pa_color']) ? $variant['attributes']['pa_color'] : '';
            $capacity = isset($variant['attributes']['pa_capacity']) ? $variant['attributes']['pa_capacity'] : '';
            $variant_title = trim($color . ' | ' . $capacity);
        ?>
            <a href="<?php echo esc_url($variant['url']); ?>" 
               class="variant-option <?php echo $is_current ? 'active' : ''; ?>">
                <div class="variant-radio">
                    <div class="radio-circle <?php echo $is_current ? 'checked' : ''; ?>"></div>
                </div>

                <div class="variant-image">
                    <?php echo $variant['image']; ?>
                </div>

                <div class="variant-info">
                    <div class="variant-title"><?php echo esc_html($variant_title); ?></div>
                    <div class="variant-stock <?php echo esc_attr($stock_class); ?>">
                        <?php echo esc_html($stock_text); ?>
                    </div>
                </div>

                <div class="variant-price">
                    <?php echo $variant['price']; ?>
                </div>

                <div class="variant-arrow">›</div>
            </a>
        <?php endforeach; ?>
    </div>
</div>