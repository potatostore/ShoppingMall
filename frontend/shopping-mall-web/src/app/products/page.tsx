const productApiUrl = 'http://localhost:8080/api/v1/products';

export default async function ProductPage(){
    const res = await fetch(productApiUrl);
    const { data: products } = await res.json();   

    return (
        <ul>
            {products.map((p) => (
                <li key={p.productId}>
                    <h3>{p.name}</h3>                            
                    <p>{p.price}</p>
                    <ul>
                        {p.productDetailResponseDTOList.map((detail) => (   
                            <li key={detail.productDetailId}>{detail.detail}</li>
                        ))}
                    </ul>
                </li>
            ))}
        </ul>
    );
}