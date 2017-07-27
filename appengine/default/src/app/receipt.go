package app

import (
	"encoding/json"

	"cloud.google.com/go/pubsub"

	"golang.org/x/net/context"

	"google.golang.org/appengine"
	"google.golang.org/appengine/log"
)

// PublishReceipt is publish to receipt topic
func PublishReceipt(c context.Context, r Receipt) error {
	client, err := pubsub.NewClient(c, appengine.AppID(c))
	if err != nil {
		log.Errorf(c, "pubsub new client err. err = %s\n", err.Error())
		return err
	}

	topic := client.Topic("receipt")

	b, err := json.Marshal(r)
	if err != nil {
		log.Errorf(c, "json marshal err. err = %s\n", err.Error())
		return err
	}

	result := topic.Publish(c, &pubsub.Message{
		Data: b,
	})
	_, err = result.Get(c)
	if err != nil {
		log.Errorf(c, "topic publish err. err = %s\n", err.Error())
		return err
	}

	return nil
}
