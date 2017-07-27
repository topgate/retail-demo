package app

import (
	"fmt"
	"math/rand"
	"net/http"
	"strconv"
	"time"

	"golang.org/x/net/context"

	"google.golang.org/appengine/log"
	"google.golang.org/appengine/taskqueue"

	"github.com/favclip/ucon"
	"github.com/favclip/ucon/swagger"
)

type receiptService struct {
}

func receiptSetup(swPlugin *swagger.Plugin) {
	s := &receiptService{}

	tag := swPlugin.AddTag(&swagger.Tag{Name: "Receipt", Description: "API of Receipt"})
	var info *swagger.HandlerInfo

	info = swagger.NewHandlerInfo(s.Publish)
	ucon.Handle("GET", "/api/1/receipt", info)
	info.Description, info.Tags = "Test Receipt Data Publish", []string{tag.Name}

	info = swagger.NewHandlerInfo(s.Burst)
	ucon.Handle("GET", "/api/1/receipt/burst", info)
	info.Description, info.Tags = "Test Receipt Data Publish Burst!!!", []string{tag.Name}
}

// Receipt is Receipt Model
type Receipt struct {
	SalesDate           time.Time `json:"salesDate"`
	StoreCode           string    `json:"storeCode"`
	RegisterID          string    `json:"registerID"`
	ReceiptNumber       int       `json:"receiptNumber"`
	ReceiptBranchNumber int       `json:"receiptBranchNumber"`
	ProductCode         string    `json:"productCode"`
	Quantity            int       `json:"quantity"`
	Price               int       `json:"price"`
}

func (s *receiptService) Publish(c context.Context) (*Receipt, error) {
	sc := rand.Intn(100) + 1
	pc := rand.Intn(1000) + 1
	d := Receipt{
		SalesDate:           time.Now(),
		StoreCode:           fmt.Sprintf("%03d", sc),
		RegisterID:          fmt.Sprintf("%d", time.Now().UnixNano()),
		ReceiptNumber:       1,
		ReceiptBranchNumber: 1,
		ProductCode:         fmt.Sprintf("%04d", pc),
		Quantity:            1,
		Price:               100 + pc,
	}
	err := PublishReceipt(c, d)
	if err != nil {
		return nil, err
	}

	return &d, nil
}

func (s *receiptService) Burst(c context.Context, r *http.Request) error {
	const taskLot = 100
	burst := 100

	burst, err := strconv.Atoi(r.FormValue("burst"))
	if err != nil {
		log.Infof(c, "burst is not number. burst = %s", r.FormValue("burst"))
	}

	log.Infof(c, "burst = %d", burst)
	for i := 0; i < burst; i++ {
		var tasks []*taskqueue.Task
		for l := 0; l < taskLot; l++ {
			tasks = append(tasks, &taskqueue.Task{
				Path:   "/api/1/receipt",
				Method: "GET",
			})
		}
		_, err := taskqueue.AddMulti(c, tasks, "default")
		if err != nil {
			log.Errorf(c, "queue add err. err = %s\n", err.Error())
			return err
		}
		log.Infof(c, "add task count = %d", (i+1)*taskLot)
	}

	return nil
}
