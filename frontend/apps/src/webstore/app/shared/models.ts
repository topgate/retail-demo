export interface Product {
    productName: string;
    productCode: string;
    price: number;
    category: {
        id: string;
        name: string;
    };
    imageURL: string;
}
