# Saga Choreography Pattern - Microservices Architecture

Bu proje, Saga Pattern'ın Choreography yaklaşımı ile geliştirilmiş bir mikroservis mimarisini temsil eder.  
Her servis, Kafka aracılığıyla olay tabanlı iletişim (event-driven communication) kurar. Sistem, dağıtık transaction yönetimini servisler arası asenkron event'ler ile sağlar.

## Kullanılan Teknolojiler

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Apache Kafka
- MySQL 8
- Lombok
- Docker (Kafka ve Zookeeper çalıştırmak için)

## Proje Yapısı

saga-choreography-pattern/
├── common-events Ortak event DTO'ları
├── order-service Sipariş oluşturur, Kafka'ya OrderCreatedEvent gönderir
├── payment-service Ödeme kontrolü yapar, PaymentCompleted/Failed event üretir
├── inventory-service Stok kontrolü yapar, InventoryReserved/Failed event üretir


## Saga Akışı

1. OrderService, Kafka’ya OrderCreatedEvent gönderir.
2. PaymentService olayı dinler, ödeme işlemini yapar:
   - Başarılıysa PaymentCompletedEvent
   - Başarısızsa PaymentFailedEvent
3. InventoryService, ödeme başarılıysa stok kontrolü yapar:
   - Başarılıysa InventoryReservedEvent
   - Başarısızsa InventoryFailedEvent
4. PaymentService, InventoryFailedEvent geldiğinde REFUND işlemi yapar (Compensation)
5. (İsteğe bağlı) OrderService, event'leri dinleyerek OrderStatus güncelleyebilir.

## API Uç Noktası

Order Oluşturma (Postman ile test):

POST /api/orders?userId=1234&amount=800

## Kompansasyon Mekanizması

- InventoryService başarısız olursa, InventoryFailedEvent gönderilir.
- PaymentService bu olayı dinleyerek ilgili ödemeyi REFUNDED statüsüne çeker.
- (İsteğe bağlı) OrderService ise CANCELLED statüsüne geçebilir.

## Projeyi Çalıştırma

1. Kafka ve Zookeeper'ı çalıştır:
docker-compose up -d


2. Her modülü ayrı terminalde çalıştır:


Not: MySQL çalışıyor olmalı. Gerekli veritabanları: 
- sso_order_db
- sso_payment_db
- sso_inventory_db

## Test Senaryoları

| Senaryo                      | Açıklama                                                |
|-----------------------------|----------------------------------------------------------|
| Ödeme başarılı, stok var    | Sipariş → ödeme tamam → stok ayrıldı                     |
| Ödeme başarısız             | Sipariş → ödeme başarısız → stok kontrolü yapılmaz       |
| Stok yetersiz               | Sipariş → ödeme tamam → stok yetersiz → ödeme refund edilir |

## Notlar

- Kafka mesajları otomatik olarak JSON formatında serileştirilir.
- Dead Letter Queue desteği için Kafka Error Handler eklenebilir.
- Her mikroservis kendi veritabanını kullanır (Database-per-Service).
