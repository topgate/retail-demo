package app

import (
	"encoding/json"
	"net/http"

	"github.com/favclip/ucon"
	"github.com/favclip/ucon/swagger"

	"golang.org/x/net/context"

	"google.golang.org/appengine/datastore"
	"google.golang.org/appengine/log"
)

type productService struct {
}

func productSetup(swPlugin *swagger.Plugin) {
	s := &productService{}

	tag := swPlugin.AddTag(&swagger.Tag{Name: "Product", Description: "API of Product"})
	var info *swagger.HandlerInfo

	info = swagger.NewHandlerInfo(s.List)
	ucon.Handle("GET", "/api/1/product", info)
	info.Description, info.Tags = "Get Product List", []string{tag.Name}

	info = swagger.NewHandlerInfo(s.Option)
	ucon.Handle("OPTIONS", "/api/1/product", info)
	info.Description, info.Tags = "Option Product", []string{tag.Name}
}

// Category is Category Model
type Category struct {
	ID   string `json:"id"`
	Name string `json:"name"`
}

func (s *productService) List(c context.Context, r *http.Request, w http.ResponseWriter) error {
	// 商品画像を用意した商品一覧
	keys := []*datastore.Key{
		datastore.NewKey(c, GetProductKind(), "0000", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0001", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0011", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0025", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0034", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0045", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0061", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0073", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0079", 0, nil),
		datastore.NewKey(c, GetProductKind(), "0087", 0, nil),
	}

	var pl = make([]*Product, len(keys))
	err := datastore.GetMulti(c, keys, pl)
	if err != nil {
		log.Errorf(c, "get from product err. err = %s\n", err.Error())
		return &HTTPError{
			Code: http.StatusInternalServerError,
			Text: err.Error(),
		}
	}

	for _, v := range pl {
		v.Category = Category{
			ID:   v.CategoryCode,
			Name: "Dummy" + v.CategoryCode,
		}
	}

	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("cache-control", "public, max-age=300")
	err = json.NewEncoder(w).Encode(pl)
	if err != nil {
		log.Errorf(c, "response json output err. err = %s\n", err.Error())
	}

	return nil
}

func (s *productService) Option(c context.Context, w http.ResponseWriter) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
	w.Header().Set("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type")
	w.Header().Set("Access-Control-Max-Age", "60")
}
