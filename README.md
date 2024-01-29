<h2 style="color:red"><b><i>Book Service API</h2>

```
main methodda 3 adet kayıt repository yoluyla veritabanına ekleniyor. 
Yani proje başlatılınca 3 kayıt mevcut.

Book book1 = new Book("Kitap1", 2013, "Yazar1", "PressName1", "Isbn1");
Book book2 = new Book("Kitap2", 2014, "Yazar2", "PressName2", "Isbn2");
Book book3 = new Book("Kitap3", 2015, "Yazar3", "PressName3", "Isbn3");

CONTROLLER PATH

[GetMapping]    /book                  # getAllBook()
[GetMapping]    /book/isbn/{isbn}      # getBookByIsbn(String isbn)
[GetMapping]    /{id}                  # getBookById(String id)
```

<h2 style="color:red"><b><i>Library-Service API</h2>

```		
CONTROLLER PATH

[PutMapping]    /library               # addBookToLibrary(AddBookRequestDto requestDto) 
[PostMapping]   /library               # createLibrary()
[GetMapping]    /library/{id}          # getLibraryById(String id)
```



<h2 style="color:red"><b><i>Ders 4</h2>

```
Api Gateway
Bizim aşırı yük alan bir servisimizin sayısını 2 ye çıkartarak gelen istekleri bölüştürebiliriz fakat 2.servisin portu
farklı olacağı için bunu da ayarlamamız gerekiyor. Bunu ayarlaması için ApiGateway kullanacağız. 
ApiGateway, kendi içerisinde LoadBalancing bulunduruyor. (LibraryService'ler api gateway'a kayıtlı zaten)

Bu loadBalancing aşağıdaki işlemleri yapıyor aslında.
ApiGateway soruyor. Ben gelen isteği hangi LibraryService'e yönlendireyim ? 2 farklı instance var çünkü.
Şuan spring cloud gateway'de kullanılan RAUNTROWİNG algoritması ile buna karar verecek.
Server side load balancing 

Benim servislerimde security katmanı varsa tüm servislere yazmam gerekecek. Ama ApiGateway geliştirirsem tüm security
katmanını 1 yerde yazabilirim. SECURITY LAYER aslında burada olmalı
istek gelir. ApiGateway, bağlı olduğu Auth servisine gidip token'i kontrol eder ona göre işlem yapar

WEB FILTER'i apiGateway'e yüklerim. buna göre servislere aspect'ler yaratabiliriz. LibraryService'ye gelen istekte apiGateway'de kontrol edilir.
Aynı zamanda apiGateway'e INTERCEPTORLER yazabilirsin. Bu interceptor aracılığıyla mesela şunu diyebiliriz. Benim mikroservislerimden
herhangi birisine bir istekte bulunulması durumunda git httpRequest'e şunu ekle.

iyi dizayn edilmiş bir apiGateway yapısında Cache yapısı olmalı. Mesela basic Authentication işlemi yapsaydık sürekli DB'ye gidip kullanıcı kontrol etmesi
çok fazla gecikme yaşanacağı anlamına gelirdi. Yazacağımız apiGateway'i iyi tasarlamamız gerekiyor. Çok yük altında olması beklenemez çooook büyük proje olması gerek. 
Öyle bir durumda bunda da loadBalancer kurulabilir.

```

![](images/genel_yapi.png)

```
Api Gateway projesini yükleyip application.yml dosyası ile gerekli configurasyonları yaptığımızı varsayalım. 
Artık hem bu apiGateway üzerinden servisimize istek atabilirken aynı zamanda direkt olarak servisimize istek atabiliyoruz.
Biz sadece apiGateway üzerinden istek atmak istiyorsak servisleri dışarıya kapatmak gerekir.
Sabit bir ip vermezsem, ip'yi sadece bilenler bağlanır. Gerekli servislere random ip ataması yapmak için server.port: 0 yapıyoruz.
Ayrıca ben birden fazla LibraryService ayağa kaldırmak istiyorum. Bu her ayağa kalkan LibraryService aynı zamanda eurekaServer'a
register olsun istiyorum ki LoadBalancing yapabilelim.
LibraryService ---> application.properties ---> server.port: 0

Spring Actuator ile yeni bir uygulama da yazabilirsin. Bu sayede kendi template'ine göre detaylı bir uygulama sağlığı, durumu vs. görebilirsin.
Kıcasacı actuator ile bir dashboard tanımlayıp sistemin o anki durumunu izleyebileceğin bir yapı yapabilirsin.

Distributed Log Trace için ZIPKIN kullanacağız.
eskiden bir dependency'idi. Şimdi bir docker image
DistributedLog neden önemli ?
Biz libraryService'yi farklı konsollardan 2 instance olacak şekilde çalıştırdık.
Mikroservis projelerinde 10 adet mikro servisimiz olsa her birinin logu için o servise gitmemiz gerekecek.
Bize toplu bir log lazım. Zipkin dağıtık logları tek yere topluyor.

Zipkin'i docker image'ı olarak indirdik. Artık servislerimizi zipkin'e ekleyelim.

Authentication apiGateway'de olmalı. 
Ya da bir authorizationServer yazarsın. ApiGateway'i de interceptor olarak kullanırsın.

gRPS ?

spring cloud config neden gerkeiyor ?
200 adet servisin var diyelim. Bu 200 servis için hem application.properties hem de application-dev.properties
dosyası olsun diyelim. Bu durumda 200 x 2 = 400 adet dosyan olur. Bir düzenleme zamanı gelince ilgili servise gidip
güncellemem gerekecek. Bunun yerine tek bir repository'de toplasam ve yapılan değişiklikte tek bir merge request açsam
daha mantıklı olmaz mı ? 200 adet merge request yerine 1 tane yaparız ama 200 değişiklik olur.
İşte biz bundan dolayı spring cloud config'e ihtiyaç duyuyoruz. 

3 farklı spring cloud config tekniği var. 
- Classpath'tan config okumak
- git üzerinden konfigurasyon
- (Hatırlamıyorum :D )

Direkt git üzerinden bir repository de oluşturabilirsin ya da projenin repository'sini de kullanabilirsin.
Hangisi best practice tartışılır
 
Bu değerler şu anda config server içerisinde
Mikroservislerin şuan erişimi yok. 
http://localhost:8888/library-service/default
http://localhost:8888/library-service/dev

ilgili mikroservis bu conf'ları alabilmesi için pom.xml'ine dependency ekleyeceğiz.
spring-cloud-starter-config

Bunu ekledikten sonra artık spring-cloud-starter-config configurasyonları yapabilirim application.properties'te
spring-cloud-config-uri=optional:configserver:http://localhost:8888/ # sonuna / koymazsan hata verir.
ayrıca biz sonuna library-service/dev felan eklemiyoruz. Bunu dinamik olarak alıyor. application-name + profile şeklinde.
application-name değiştirirsen conf'ların adını da değişmen gerekir.

burada optional dememizin nedeni resiliance diyoruz ya uygulama hiç çökmesin dirençli uygulama olmasını istediğimiz için.
```

<h2 style="color:red"><b><i>Ders 6 Vault</h2>

```
secret işlemlerini vault'ta yapıyoruz. Bize daha avantajlı ve daha güvenli bir yapı sunuyor.
Mesela kubernetes cluster kuruyorsan, bu cluster'a direkt uygulama içerisnde kullanılan secretleri kubernetes 
secret olarak vault üzerinden taşıyabilirsin.

Biz bir önceki derste config ile properties verilerine git üzerinden ulaşıyorduk. Bizim config repoya ulaşan
herkes bu propertylere ulaşabilir. Sensitive dataları böyle herkesin ulaşabileceği yerlerde saklamak riskli.
Vault, secretManager ya da kubernetes secrets tarzı yerlerde saklamak lazım.
```

<h2 style="color:red"><b><i>NOTLAR</h2>

```
yapılan herhangi bir değişiklik başka bir mikroservisi etkiliyorsa o mikroservis değildir.
servisler resilience olmak zorunda. mesela bookService erişilemez ya da çökerse bile libraryService dayanıklı olup düşmemesi gerekir.
kubernetes içinde 1.öncelik ver eurekaServer'ı
bookService, eurekaServer'a register oldu. libraryService'de eurekaServer'a discover oldu.

LibraryService için mikroservis dependency'leri
 - OpenFeign (LibraryService içerisinde FeignClient yaratmak için.)
 - Eureka Discovery Client (LibraryService'i de register edeceğiz.)
 
BookService için mikroservis dependency'leri
 - OpenFeign (LibraryService içerisinde FeignClient yaratmak için.)
 - Eureka Discovery Client (LibraryService'i de register edeceğiz.)
 

 
Ben bookService'yi sadece veriyi dışarı açmak için kullanacağım. (diğer mikroservislere)
BookService
 - List<BookDto> getAllBooks()
 - BookIdDto findByIsbn(String isbn)
 
LibraryService'den book sorguladığım zaman bir endpoint sadece bookId dönecek. Diğeri de bookDetails dönecek.
BookIdDto
 - id
 - isbn 
 
BookMain (Insertions)
 - new Book()
 
 
OneToMany durumlarında mesela Library içerisinde List<Book> books yerine List<String> bookId ile sadece bookId paylaşımı yapacağım.

Eureka, client side loadBalancing için kullanılıyor buna bir daha bak emin değilim.
EurekaServer'da service discovery orcestration
service discovery sadece eureka'da diğer herşey kubernetes'te yapılabilir ama çok mantıklı bir yaklaşım değil.
Kubernetes'e geçiyorsam tüm orcestration'u birine aktarmak lazım.

Kubernetes'in service discovery için istio var. Bu hem loadBalancing yapıyor hem faultTolerance hem resilience hem de service discovery
ama eurekaserver ne yapıyor sadece service discovery yapıyor.

istio               vs          eureka
serviceDiscovery                ServiceDiscovery
loadBalancing
faultTolerance
resilience

Ben Library'e book eklemek için bookService'ye ulaşmam gerekir. Bunun için bookService'yi Eureka'ya register etmeliyim.
EurekaServer yoksa önce eurekaServer'i oluşturalım.

```



<h2 style="color:red"><b><i>Mikroservisler arası iletişim</h2>

```
EurekaServer, bizim mikroservisler arasındaki iletişimimizi sağlar. Bağımsız uygulamalar olduğu için birbirlerini tanımazlar. Biz tanıtırız. 
Bunu HTTP Rest api'ler ile gerçekleştirir. (GRPS ya da SOAP'ta olabilirdi.)
Arada REST API varsa genelde kullanılan 2 opsiyon var. Bunlar RestTemplate ve FeignClient 
Hangi servis verilerini expose edecekse onu Eureka'ya register etmeliyiz.
Bizim projede LibraryService, BookService'ye erişebilmeli ki id ile kitapları çekebilsin. Bundan dolayı BookService'yi register etmeliyiz.
BookService, LibraryService'ye ihtiyacı olmadığı için registere gerek yok.(erişmek derken istek atma durumu yok)
```

<h2 style="color:red"><b><i>Eureka Server Oluşturma</h2>

```
spring projesi oluştur. Dependencyler
 - EurekaServer # spring-cloud-starter-netflix-eureka-server
 
daha sonra main metodunun olduğu class'a 
@EnableEurekaServer 
anotasyonunu ekle.

application.properties dosyasına da şunları ekle.

server.port=8761 # Default 
eureka.client.register-with-eureka=false # Client'ler eureka'yı register etsin mi ?
eureka.client.fetch-registry=false # Client'ler kendi başına registry'i yakalasın mı ? hayır eureka'ya sorulacak.

Daha sonra projeyi başlatıp
localhost:8761
üzerinden eureka serveri kontrol edebilirsin.
```

<h2 style="color:red"><b><i>Bir servisi Eureka'ya client olarak bağlamak</h2>
```
projenin main metodunun olduğu class'a gelerek
@EnableEurekaClient 
ekleyebilirsin. (Servisi eureka'ya register etmek için kullandığımız anotasyon.)

application.properties'e ise
server.port=0 # Random oluşturulacak port eurekaServer'da saklıdır. Bundan dolayı dışarı açmamış oluyoruz aslında.
spring.application.name=book-service
eureka.instance.prefer-ip-address=true # Eureka'ya ip adresini kullan diyoruz.
eureka.instance.instance-id=${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${random.value}}
eureka.client.service-url.default-zone=${EUREKA_URI:http://localhost:8761/eureka}
```

<h2 style="color:red"><b><i>Client olarak bağlanmış servise başka bir servis nasıl ulaşabilir? (FeignClient)</h2>

```
Birkaç farklı yöntem var. Biz feignClient kullanacağız.

İlgili servis için dependency'ler (Bizim için LibraryService)
 - Eureka Discovery Client # spring-cloud-starter-netflix-eureka-client (EurekaServer'a register olması için)
 - OpenFeign # spring-cloud-starter-openfeign (service içerisinde FeignClient yaratmak için.)
 
Daha sonra LibraryService'nin main class'ına @EnableFeignClients anotasyonunu ekleyerek bir bean olarak spring application
context'te görünmesini sağlarız.

@EnableFeignClient Register edilen servisi kullanmak istiyorsak (Bizim senaryoda LibraryService'nin BookService'yi kullanabilmesi
için LibraryService'ye ekliyoruz.) için kullandığımız anotasyon.  bir mikro servisin başka bir mikro servisi çağırmak için kullanılan bir HTTP istemci kütüphanesidir. 
 
client paketi oluşturup altına BookServiceClient interface'i oluştur.
BookServiceClient interface'ine @FeignClient(params) anotasyonunu ekledikten sonra artık bir feignClient olur.

@FeignClient(
    name="book-service", # Burası eureka'dan almak istediğimiz servisin adı. BookService'de application.properties'te tanımladığımız Register Name (spring.application.name)
    path="/v1/book" # ilgili servisin path'ı
)

Daha sonra bu interface içerisinde book-service'de hangi endpoint'lere ulaşmak istiyorsam onları yazıyorum.
NOT: Eğer aşağıdaki gibi book'taki DTO'lardan döndereceksen bu dto'ları da libraryService'de tanımlarız.
Biz BookResponseDto yazmasak JSON olarak döner. BookResponseDto yazdığımız için gelen JSON'u BookResponseDto'ya parse eder.

@GetMapping("/{id}")
@CircuitBreaker(name = "getBookByIdCircuitBreaker", fallbackMethod = "getBookByIdFallback")
ResponseEntity<BookResponseDto> getBookById(@PathVariable String id);

Daha sonra libraryService'de getAllBooksInLibraryById(String id) metodunda bookService'den kitapları alacağımız zaman
library.getUserBookId() # Burası db'ye kaydettiğimiz id'ler. getUserBookId diye isimlendirebiliriz.
        .stream()
        .map(bookServiceClient::getBookById) # Bu noktada feignClient çalışacak
        .map(ResponseEntity::getBody)
        .collect(Collectors.toList())
 
```

<h2 style="color:red"><b><i>Bizim senaryomuzda neden client olarak bağlanmış servise bağlandık ?</h2>

```
Library'nin veritabanına library entity eklerken sadece libraryId ve o library'e ait kitapların id'sini liste şeklinde tuttum.
Ben tüm kitap bilgisini library db'ye atmayayım. Sadece kitap id'lerini atayım. Ne zaman api ile döneceksem o zaman gidip
feignClient aracılığıyla bookService'yi çağırıp detaylarını çekeyim.

Biz bunun için bookService'de getBookById'yi kullanacağız.
```

<h2 style="color:red"><b><i>Peki 2 servis iletişiminde bir hata durumunda ne olacak?</h2>

```
Bunun için LibraryService/client/RetrieveMessageErrorDecoder'a bakabilirsin. Açıklamalarıyla birlikte yazdım.
```










<h2 style="color:red"><b><i>Fault Tolerance</h2>

```
Fault Tolerance
Biz normalde hata gönderiyoruz ya ExceptionMessage şeklinde, alınan hataya göre farklı bir process işlendiği olaya fault tolerance diyoruz.
Biz hata aldığımızda yeni bir process oluşturmak için

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-circuitbreaker-resilience4j</artifactId>
        </dependency>

dependency'sini Library_Service'e ekledik
NOT: Bir projede mesela a servisi b servisinde hata fırlattırırken
b servisi a servisinde fault tolerance kullanabilir.

MethodNotAllowed(405) bir hatadır. Logger.error() çümkü kod içerisinde bir şey hatalı ama gelen parametreye istinaden hatalar
Logger info'dur
```


