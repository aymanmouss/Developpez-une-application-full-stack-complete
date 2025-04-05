(function($) {
    'use strict';

    class ProductGroups {
        constructor() {
            this.initializeEventListeners();
        }

        initializeEventListeners() {
            $('#product-variant').on('change', this.handleVariantChange.bind(this));
        }

        handleVariantChange(event) {
            const $select = $(event.target);
            const selectedUrl = $select.val();

            if (!selectedUrl) {
                return;
            }

            // Add loading state
            $select.prop('disabled', true)
                   .closest('.product-variants')
                   .addClass('loading');

            // Redirect to selected variant
            window.location.href = selectedUrl;
        }
    }

    // Initialize when document is ready
    $(document).ready(() => {
        new ProductGroups();
    });

})(jQuery);