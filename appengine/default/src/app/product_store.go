package app

import (
	"encoding/json"

	"golang.org/x/net/context"

	"google.golang.org/appengine/datastore"
	"google.golang.org/appengine/log"
	"google.golang.org/appengine/memcache"
)

// Product is Product Model
type Product struct {
	ProductCode  string   `json:"productCode"`
	ProductName  string   `json:"productName"`
	CategoryCode string   `json:"-"`
	Category     Category `json:"category" datastore:"-"`
	Price        int      `json:"price"`
	ImageURL     string   `json:"imageURL"`
}

// GetProductKind is Get Product Kind Name
func GetProductKind() string {
	return "Product"
}

// GetProduct is Product get from Datastore
func GetProduct(c context.Context, productCode string) (*Product, error) {
	var p Product
	pk := datastore.NewKey(c, GetProductKind(), productCode, 0, nil)
	item, err := memcache.Get(c, pk.Encode())
	if err == nil {
		err = json.Unmarshal(item.Value, &p)
		if err != nil {
			log.Errorf(c, "json.Unmarshal err. err = %s", err.Error())
			return nil, err
		}
	} else {
		if err == memcache.ErrNotStored || err == memcache.ErrCacheMiss {
			err := datastore.Get(c, pk, &p)
			if err != nil {
				if err == datastore.ErrNoSuchEntity {
					return nil, err
				}
				log.Errorf(c, "datastore.Get err. err = %s", err.Error())
				return nil, err
			}
			b, err := json.Marshal(p)
			if err != nil {
				log.Errorf(c, "json.Marshal err. err = %s", err.Error())
				return nil, err
			}
			item = &memcache.Item{
				Key:   pk.Encode(),
				Value: b,
			}
			err = memcache.Set(c, item)
			if err != nil {
				log.Errorf(c, "memcache.Set err. err = %s", err.Error())
				return nil, err
			}
		} else {
			return nil, err
		}
	}

	return &p, nil
}
