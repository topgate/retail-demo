package app

import (
	"testing"

	"google.golang.org/appengine/aetest"
	"google.golang.org/appengine/datastore"
)

func TestGetProduct(t *testing.T) {
	c, done, err := aetest.NewContext()
	if err != nil {
		t.Fatal(err)
	}
	defer done()

	const ProductCode = "0000"

	p := Product{
		ProductName:  "Sample",
		CategoryCode: "100",
		Price:        100,
	}
	_, err = datastore.Put(c, datastore.NewKey(c, "Product", ProductCode, 0, nil), &p)
	if err != nil {
		t.Fatal(err)
	}

	// 複数回呼んでも問題ないことを確認
	for i := 0; i < 5; i++ {
		_, err = GetProduct(c, ProductCode)
		if err != nil {
			t.Fatal(err)
		}
	}
}

func TestGetProductNotFound(t *testing.T) {
	c, done, err := aetest.NewContext()
	if err != nil {
		t.Fatal(err)
	}
	defer done()

	const ProductCode = "0000"

	_, err = GetProduct(c, ProductCode)
	if err != datastore.ErrNoSuchEntity {
		t.Fatal(err)
	}
}
