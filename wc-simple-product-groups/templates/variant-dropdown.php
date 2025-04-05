<?php
if (!defined('ABSPATH')) exit;

$current_product_id = $product->get_id();
?>

<div class="product-variant-selector">
    <button class="variant-selector-header">
        Couleur et capacité: 
        <span class="selected-variant"><?php 
            $current_color = $product->get_attribute('pa_color');
            $current_capacity = $product->get_attribute('pa_capacity');
            echo esc_html($current_color . ' | ' . $current_capacity);
        ?></span>
        <span class="chevron">▼</span>
    </button>
    
    <div class="variant-list-container">
        <?php foreach ($variants as $variant): 
            $color = isset($variant['attributes']['pa_color']) ? $variant['attributes']['pa_color'] : '';
            $capacity = isset($variant['attributes']['pa_capacity']) ? $variant['attributes']['pa_capacity'] : '';
            $is_current = $variant['id'] === $current_product_id;
            $stock_text = $variant['stock_status'] === 'instock' ? 'En stock' : 'Hors stock';
        ?>
            <a href="<?php echo esc_url($variant['url']); ?>" 
               class="variant-item <?php echo $is_current ? 'active' : ''; ?>">
                <div class="variant-main">
                    <img src="<?php echo esc_url($variant['image_url']); ?>" 
                         alt="<?php echo esc_attr($color); ?>" 
                         class="variant-thumb" 
                         width="30" 
                         height="30">
                    <div class="variant-info">
                        <span class="variant-title"><?php echo esc_html($color . ' | ' . $capacity); ?></span>
                        <span class="stock-status <?php echo $variant['stock_status'] === 'instock' ? 'in-stock' : 'out-of-stock'; ?>">
                            <?php echo esc_html($stock_text); ?>
                        </span>
                    </div>
                </div>
                <div class="variant-price">
                    <?php echo $variant['price']; ?>
                    <span class="arrow">›</span>
                </div>
            </a>
        <?php endforeach; ?>
    </div>
</div>