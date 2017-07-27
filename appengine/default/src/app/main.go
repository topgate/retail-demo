package app

import (
	"fmt"
	"math/rand"
	"mime"
	"net/http"
	"reflect"
	"strings"
	"time"

	"github.com/favclip/ucon"
	"github.com/favclip/ucon/swagger"

	"google.golang.org/appengine"
)

func init() {
	var _ ucon.HTTPErrorResponse = &HTTPError{}

	rand.Seed(time.Now().UTC().UnixNano())

	mime.AddExtensionType(".json", "application/json;charset=utf-8")
	mime.AddExtensionType(".map", "application/json;charset=utf-8")

	http.HandleFunc("/_ah/start", func(w http.ResponseWriter, r *http.Request) {
		// noop
	})

	ucon.Middleware(UseAppengineContext)
	ucon.Orthodox()

	swPlugin := swagger.NewPlugin(&swagger.Options{
		Object: &swagger.Object{
			Info: &swagger.Info{
				Title:   "Retail Demo",
				Version: "v1",
			},
			Schemes: []string{"http", "https"},
		},
		DefinitionNameModifier: func(refT reflect.Type, defName string) string {
			if strings.HasSuffix(defName, "JSON") {
				return defName[:len(defName)-4]
			}
			return defName
		},
	})
	ucon.Plugin(swPlugin)

	// handler
	orderSetup(swPlugin)
	receiptSetup(swPlugin)
	productSetup(swPlugin)

	ucon.DefaultMux.Prepare()
	http.Handle("/", ucon.DefaultMux)
}

// HTTPError is response JSON.
type HTTPError struct {
	Code int    `json:"code"`
	Text string `json:"text"`
}

// Error returns error string.
func (err *HTTPError) Error() string {
	return fmt.Sprintf("status %d: %s", err.Code, err.Text)
}

// StatusCode returns http status code.
func (err *HTTPError) StatusCode() int {
	return err.Code
}

// ErrorMessage returns itself. it uses to respone payload.
func (err *HTTPError) ErrorMessage() interface{} {
	return err
}

// UseAppengineContext replace Bubble.Context by appengine's context.Context.
// This middleware must append before the NetContextDI (it contains in ucon.Orthodox()).
func UseAppengineContext(b *ucon.Bubble) error {
	if b.Context == nil {
		b.Context = appengine.NewContext(b.R)
	} else {
		b.Context = appengine.WithContext(b.Context, b.R)
	}

	return b.Next()
}
