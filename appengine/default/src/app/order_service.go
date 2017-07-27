package app

import (
	"fmt"
	"net/http"
	"time"

	"github.com/favclip/ucon"
	"github.com/favclip/ucon/swagger"

	"golang.org/x/net/context"
	"google.golang.org/appengine/datastore"
	"google.golang.org/appengine/log"
)

type orderService struct {
}

func orderSetup(swPlugin *swagger.Plugin) {
	s := &orderService{}

	tag := swPlugin.AddTag(&swagger.Tag{Name: "Order", Description: "API of Order"})
	var info *swagger.HandlerInfo

	info = swagger.NewHandlerInfo(s.Post)
	ucon.Handle("POST", "/api/1/order", info)
	info.Description, info.Tags = "Post Order", []string{tag.Name}

	info = swagger.NewHandlerInfo(s.Option)
	ucon.Handle("OPTIONS", "/api/1/order", info)
	info.Description, info.Tags = "Option Order", []string{tag.Name}
}

// OrderPostRequest is POST request
type OrderPostRequest struct {
	StoreCode   string `json:"storeCode"`
	ProductCode string `json:"productCode"`
	Quantity    int    `json:"quantity"`
}

// OrderPostResponse is Result for API
type OrderPostResponse struct {
	StoreCode   string    `json:"storeCode"`
	RegisterID  string    `json:"registerId"`
	ProductCode string    `json:"productCode"`
	Price       int       `json:"price"`
	Quantity    int       `json:"quantity"`
	CreatedAt   time.Time `json:"createdAt"`
}

func (s *orderService) Post(c context.Context, form *OrderPostRequest, w http.ResponseWriter) (*OrderPostResponse, error) {
	p, err := GetProduct(c, form.ProductCode)
	if err != nil {
		if err == datastore.ErrNoSuchEntity {
			return nil, &HTTPError{Code: http.StatusNotFound, Text: ""}
		}
		log.Errorf(c, "GetProduct err = %s\n", err.Error())
		return nil, &HTTPError{Code: http.StatusInternalServerError, Text: err.Error()}
	}

	d := Receipt{
		SalesDate:           time.Now(),
		StoreCode:           form.StoreCode,
		RegisterID:          fmt.Sprintf("%d", time.Now().UnixNano()),
		ReceiptNumber:       1,
		ReceiptBranchNumber: 1,
		ProductCode:         form.ProductCode,
		Quantity:            form.Quantity,
		Price:               p.Price * form.Quantity,
	}
	ch := make(chan error, 1)
	go func() {
		err = PublishReceipt(c, d)
		ch <- err
	}()
	select {
	case err := <-ch:
		w.Header().Set("Access-Control-Allow-Origin", "*")
		if err != nil {
			return nil, &HTTPError{
				Code: http.StatusInternalServerError,
				Text: err.Error(),
			}
		}

		return &OrderPostResponse{
			StoreCode:   d.StoreCode,
			RegisterID:  d.RegisterID,
			ProductCode: form.ProductCode,
			Price:       d.Price,
			Quantity:    form.Quantity,
			CreatedAt:   time.Now(),
		}, nil
	case <-time.After(time.Second * 8):
		w.Header().Set("Access-Control-Allow-Origin", "*")
		return nil, &HTTPError{
			Code: http.StatusInternalServerError,
			Text: "Timeout",
		}
	}
}

func (s *orderService) Option(c context.Context, w http.ResponseWriter) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
	w.Header().Set("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type")
	w.Header().Set("Access-Control-Max-Age", "60")
}
