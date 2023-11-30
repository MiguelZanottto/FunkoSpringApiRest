db.createUser({
    user: 'admin',
    pwd: 'adminPassword123',
    roles: [
        {
            role: 'readWrite',
            db: 'tienda',
        },
    ],
});

db = db.getSiblingDB('tienda');

db.createCollection('pedidos');

db.pedidos.insertMany([
    {
        "id": "6566a7346aae4b5ae058eb8c",
        "idUsuario": 1,
        "cliente": {
            "nombreCompleto": "Miguel Zanotto",
            "email": "migzanoto18@hotmail.com",
            "telefono": "722663185",
            "direccion": {
                "calle": "Avd",
                "numero": "73",
                "ciudad": "Madrid",
                "provincia": "Comunidad de Madrid",
                "pais": "Españita",
                "codigoPostal": "28019"
            }
        },
        "lineasPedido": [
            {
                "cantidad": 1,
                "idFunko": 1,
                "precioFunko": 19.99,
                "total": 19.99
            },
            {
                "cantidad": 1,
                "idFunko": 2,
                "precioFunko": 14.99,
                "total": 14.99
            },
            {
                "cantidad": 1,
                "idFunko": 3,
                "precioFunko": 16.99,
                "total": 16.99
            }
        ],
        "totalItems": 3,
        "total": 51.97,
        "fechaCreacion": "2023-11-29T03:51:32.8959486",
        "fechaActualizacion": "2023-11-29T03:51:32.8959486",
        "isActivo": true
    }
    ]);