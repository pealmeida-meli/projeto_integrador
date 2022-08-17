# Requisito 6
API REST desenvolvida pelo grupo Beta Campers para o Projeto Integrador feito durante o IT Bootcamp Backend Java (wave 6).

<img src="purchase-history.png" alt="UML diagram">

# Funcionalidades

## Purchase History

`GET /api/v1/purchase-history` <br>
Busca histórico de compras. Exemplo de Response:
<br>
<pre><code>{
    "items": [
        {
            "productName": "Iogurte",
            "productPrice": 10.99,
            "sellerName": "Maria",
            "sellerEmail": "maria@example.com",
            "timestamp": "2022-08-16T22:33:32.683747",
            "quantity": 2,
            "total": 21.98
        },
        {
            "productName": "Margarina",
            "productPrice": 7.50,
            "sellerName": "José",
            "sellerEmail": "jose@example.com",
            "timestamp": "2022-08-16T22:33:32.683747",
            "quantity": 5,
            "total": 37.50
        }
    ],
    "currentPage": 1,
    "perPage": 20,
    "firstPage": 1,
    "lastPage": 1,
    "totalElements": 2
}
</code></pre>
