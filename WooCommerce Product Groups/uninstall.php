<?php
if (!defined('WP_UNINSTALL_PLUGIN')) {
    exit;
}

// Delete plugin options
delete_option('wc_product_groups_version');

// Clean up taxonomy terms
$terms = get_terms(array(
    'taxonomy' => 'product_group',
    'hide_empty' => false,
));

if (!empty($terms) && !is_wp_error($terms)) {
    foreach ($terms as $term) {
        wp_delete_term($term->term_id, 'product_group');
    }
}

// Clear any