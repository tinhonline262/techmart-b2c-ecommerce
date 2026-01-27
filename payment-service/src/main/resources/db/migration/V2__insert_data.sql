-- Insert default payment providers
INSERT INTO payment_provider
(id, enabled, name, configure_url, landing_view_component_name)
VALUES
    ('COD', true, 'Cash on Delivery', NULL, 'CODCheckout'),
    ('VNPAY', true, 'VNPay', 'https://sandbox.vnpayment.vn/paymentv2/vpcpay.html', 'VNPayCheckout'),
    ('MOMO', true, 'MoMo', 'https://test-payment.momo.vn/v2/gateway/api/create', 'MoMoCheckout'),
    ('PAYPAL', false, 'PayPal', 'https://api-m.sandbox.paypal.com/v2/checkout/orders', 'PayPalCheckout');
